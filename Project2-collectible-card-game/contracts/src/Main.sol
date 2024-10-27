// SPDX-License-Identifier: MIT
pragma solidity ^0.8;

import "./Struct.sol";
import "./Booster.sol";
import "./Collection.sol";
import "./Ownable.sol";
import "./Trading.sol";

contract Main is Ownable {
  event NewCollection(uint collectionId, string name, uint cardCount);
  event NewBooster(uint boosterId, string name);
  event BoosterOpened(address indexed user, Card[] cards);
  event NewTrade(uint boosterId, string name);

  uint private count;
  uint private countBooster;
  uint private countCardTrade;
  mapping(string => Booster) private boosters;
  mapping(string => Trading) private tradings;
  mapping(string => Collection) private collections;
  string[] private collectionNames;
  string[] private boosterKeys;

  constructor() Ownable(0x8626f6940E2eb28930eFb4CeF49B2d1F2C9C1199){
    count = 0;
    countBooster = 0;
    countCardTrade = 0;
  }

  function createCollection(string memory name, uint cardCount) internal {
    address initialOwner = address(this);
    collections[name] = new Collection(initialOwner, name, cardCount);
    collectionNames.push(name);
    tradings[name] = new Trading(address(this), collections[name]);
    emit NewCollection(count, name, cardCount);
    count++;
  }

  function getCardsByOwner(address _owner) external view returns (Card[] memory) {
    uint totalCards = 0;
    for (uint i = 0; i < collectionNames.length; i++) {
      Collection collection = collections[collectionNames[i]];
      totalCards += collection.balanceOf(_owner);
    }
    Card[] memory result = new Card[](totalCards);
    uint cpt = 0;
    for (uint i = 0; i < collectionNames.length; i++) {
      Collection collection = collections[collectionNames[i]];
      Card[] memory cards = collection.getCards(_owner);
      for (uint j = 0; j < cards.length; j++) {
        result[cpt] = cards[j];
        cpt++;
      }
    }
    return result;
  }

  function createAndAssignCard(string memory _num, string memory _img, string memory _name, uint _cardCount, address _to) internal {
    if(address(collections[_name]) == address (0)) {
      createCollection(_name, _cardCount);
    }
    Collection collection = collections[_name];
    bool exist = collection.existCard(_num);
    if(!exist) {
      collection.createCard(_num, _img);
    }
    uint tokenId = collection.getTokenIdByCardNum(_num);
    address currentOwner = collection.ownerOf(tokenId);
    if (currentOwner != _to) {
      collection.transfer(_to, tokenId);
    }
  }

  function createAndAssignCards(string[] memory _num, string[] memory _img, string memory _name, uint _cardCount, address _to) onlyOwner external {
    require(_to != owner, unicode"L'administrateur ne peut pas transférer la propriété à lui-meme");
    for (uint i = 0; i <_num.length; i++) {
      createAndAssignCard(_num[i], _img[i], _name, _cardCount, _to);
    }
  }

  function getAllKey() external view returns (string[] memory) {
    return boosterKeys;
  }

  function createBoosterCards(string[] memory _num, string[] memory _img, string memory _name, string memory _nameCollection, uint _cardCount) onlyOwner external {
    require(_num.length == 3, "Booster non rempli ou maximum atteint");
    require(address(boosters[_name]) == address(0), "booster existant");
    createBooster(_name);
    Booster currentBooster = boosters[_name];
    for (uint i = 0; i <_num.length; i++) {
      currentBooster.createBoosterCard(_num[i], _img[i], _nameCollection, _cardCount);
    }
  }

  function createBooster(string memory name) internal {
    address  initialOwner = address (this);
    boosters[name] = new Booster(initialOwner, name);
    boosterKeys.push(name);
    emit NewBooster(countBooster, name);
    countBooster++;
  }

  function openBooster(string memory name, address _to) external {
    require(address(boosters[name]) != address(0), "booster inexistant");
    require(msg.sender != owner, unicode"L'administrateur ne peut pas transférer la propriété à lui-meme");
    Booster current = boosters[name];
    Card[] memory result = new Card[](current.max());
    for(uint i = 0; i < current.max(); i++) {
      (string memory num, string memory img, string memory nameCollection, uint cardCountCollection) = current.boosterCards(i);
      createAndAssignCard(num, img, nameCollection, cardCountCollection, _to);
      result[i] = Card(num, img, nameCollection, cardCountCollection);
    }
    delete boosters[name];
    emit BoosterOpened(_to, result);
  }

  function listCardForSale(string memory collectionName, string memory cardId) external {
    tradings[collectionName].listCardForSale(cardId);
    countCardTrade++;
  }

  function buyCard(string memory collectionName, uint256 saleId) external payable {
    tradings[collectionName].buyCard(saleId);
  }

  function removeSale(string memory collectionName, uint256 saleId) external {
    tradings[collectionName].removeSale(saleId);
  }

  function getAllSales() external view returns (uint256[] memory ids, Card[] memory cards) {
    uint256[] memory saleIds = new uint256[](countCardTrade);
    Card[] memory cardsSale = new Card[](countCardTrade);
    uint cpt = 0;
    for (uint i = 0; i < collectionNames.length; i++) {
      Trading trade = tradings[collectionNames[i]];
      (ids, cards) = trade.getAllSale();
      for(uint j = 0; j < cards.length; j++) {
        saleIds[cpt] = ids[j];
        cardsSale[cpt] = cards[j];
        cpt++;
      }
    }
    return (saleIds, cardsSale);
  }
}
