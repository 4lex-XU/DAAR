// SPDX-License-Identifier: MIT
pragma solidity ^0.8;

import "./Struct.sol";
import "./Booster.sol";
import "./Collection.sol";
import "./Ownable.sol";

contract Main is Ownable {
  event NewCollection(uint collectionId, string name, uint cardCount);
  event NewBooster(uint boosterId, string name);
  event BoosterOpened(address indexed user, Card[] cards);

  uint private count;
  uint private countBooster;
  mapping(uint => Collection) private collections;
  mapping(string => Booster) private boosters;
  mapping(uint => string) private boostersKeys;

  constructor() Ownable(0x8626f6940E2eb28930eFb4CeF49B2d1F2C9C1199){
    count = 0;
  }

  function createCollection(string memory name, uint cardCount) internal returns (uint) {
    address initialOwner = address(this);
    collections[count] = new Collection(initialOwner, name, cardCount);
    emit NewCollection(count, name, cardCount);
    count++;
    return count-1;
  }

  function getCollectionById(uint _id) internal view returns (Collection) {
    require(_id < count, "Collection non existante");
    return collections[_id];
  }

  function getIdCollectionByName(string memory name) internal view returns (uint) {
    for(uint i = 0; i < count; i++) {
      if (keccak256(abi.encodePacked(collections[i].name())) == keccak256(abi.encodePacked(name))) {
        return i;
      }
    }
    return type(uint256).max;
  }

  function getCardsByOwner(address _owner) external view returns (Card[] memory) {
    uint totalCards = 0;
    for (uint i = 0; i < count; i++) {
      Collection collection = getCollectionById(i);
      totalCards += collection.balanceOf(_owner);
    }
    Card[] memory result = new Card[](totalCards);
    uint cpt = 0;
    for (uint i = 0; i < count; i++) {
      Collection collection = getCollectionById(i);
      Card[] memory cards = collection.getCards(_owner);
      for (uint j = 0; j < cards.length; j++) {
        result[cpt] = cards[j];
        cpt++;
      }
    }
    return result;
  }

  function createAndAssignCard(string memory _num, string memory _img, uint _id, address _to) internal {
    Collection collection = getCollectionById(_id);
    bool exist = collection.existCard(_num);
    if(!exist) {
      collection.createCard(_num, _img);
    }
    collection.transfer(_to, collection.getTokenIdByCardNum(_num));
  }

  function createAndAssignCards(string[] memory _num, string[] memory _img, string memory _name, uint _cardCount, address _to) onlyOwner external {
    require(_to != owner, unicode"L'administrateur ne peut pas transférer la propriété à lui-meme");
    uint idCategory =  getIdCollectionByName(_name);
    if(idCategory == type(uint256).max) {
      idCategory = createCollection(_name, _cardCount);
    }
    for (uint i = 0; i <_num.length; i++) {
      createAndAssignCard(_num[i], _img[i], idCategory, _to);
    }
  }

  function getAllKey() external view returns (string[] memory) {
    string[] memory keys = new string[](countBooster);
    for(uint i = 0; i < countBooster; i++) {
      if(address(boosters[boostersKeys[i]]) != address(0)) {
        keys[i] = boostersKeys[i];
      }
    }
    return keys;
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
    boostersKeys[countBooster] = name;
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
      uint idCategory =  getIdCollectionByName(nameCollection);
      if(idCategory == type(uint256).max) {
        idCategory = createCollection(nameCollection, cardCountCollection);
      }
      createAndAssignCard(num, img, idCategory, _to);
      result[i] = Card(num, img, nameCollection, cardCountCollection);
    }
    delete boosters[name];
    emit BoosterOpened(_to, result);
  }
}
