// SPDX-License-Identifier: MIT
pragma solidity ^0.8;

import "./Struct.sol";
import "./Collection.sol";
import "./Ownable.sol";

contract Main is Ownable {

  event NewCollection(uint collectionId, string name, uint cardCount);

  uint private count;
  mapping(uint => Collection) private collections;

  constructor() Ownable(){
    count = 0;
  }

  fallback() external payable {
  }

  receive() external payable {
  }

  function createCollection(string memory name, uint cardCount) private {
    collections[count] = new Collection(name, cardCount);
    emit NewCollection(count, name, cardCount);
    count++;
  }

  function getCollectionById(uint _id) internal view returns (Collection) {
    require(count > 0, "Aucune collection existante");
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

  function getTotalCollections() internal view returns (uint) {
    require(count > 0, "Aucune collection existante");
    return count;
  }

  function getCardsByOwner(address _owner) external view returns (Card[] memory) {
    uint totalCards = 0;
    for (uint i = 0; i < getTotalCollections(); i++) {
      Collection collection = getCollectionById(i);
      totalCards += collection.balanceOf(_owner);
    }

    Card[] memory result = new Card[](totalCards);
    uint cpt = 0;
    for (uint i = 0; i < getTotalCollections(); i++) {
      Collection collection = getCollectionById(i);
      Card[] memory cards = collection.getCards(_owner);
      for (uint j = 0; j < cards.length; j++) {
        result[cpt] = cards[j];
        cpt++;
      }
    }
    return result;
  }

  function createAndAssignCard(string memory _num, string memory _img, uint _id, address _to) private {
    Collection collection = getCollectionById(_id);
    collection.createCard(_num, _img);
    collection.transfer(_to, collection.getTokenIdByCardNum(_num));
  }

  function createAndAssignCards(string[] memory _num, string[] memory _img, string memory _name, uint _cardCount, address _to) external {
    require(_num.length == _img.length, "Taille des tableaux non correspondante");
    uint idCategory =  getIdCollectionByName(_name);
    if(idCategory == type(uint256).max) {
      createCollection(_name, _cardCount);
    }

    for (uint i = 0; i <_num.length; i++) {
      createAndAssignCard(_num[i], _img[i], idCategory, _to);
    }
  }
}
