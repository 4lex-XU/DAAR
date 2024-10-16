// SPDX-License-Identifier: MIT
pragma solidity ^0.8;

import "./CardStruct.sol";
import "./Collection.sol";
import "./Ownable.sol";

contract Main is Ownable{

  event NewCollection(uint collectionId, string name, uint cardCount);

  uint private count;
  mapping(uint => Collection) private collections;

  /*
  constructor(address _owner) Ownable(_owner){
    count = 0;
  }
  */

  constructor(){
    count = 0;
  }

  function createCollection(string calldata name, uint cardCount) external onlyOwner {
    collections[count] = new Collection(name, cardCount);
    emit NewCollection(count, name, cardCount);
    count++;
  }

  function getCollection(uint _id) internal view returns (Collection) {
    require(_id < count, "Collection non existante");
    return collections[_id];
  }

  function getTotalCollections() internal view returns (uint) {
    return count;
  }

  function getCardsByOwner(address _owner) external view returns (Card[] memory) {
    uint totalCards = 0;
    for (uint i = 0; i < getTotalCollections(); i++) {
      Collection collection = getCollection(i);
      totalCards += collection.balanceOf(_owner);
    }

    Card[] memory result = new Card[](totalCards);
    uint cpt = 0;
    for (uint i = 0; i < getTotalCollections(); i++) {
      Collection collection = getCollection(i);
      Card[] memory cards = collection.getCards(_owner);
      for (uint j = 0; j < cards.length; j++) {
        result[cpt] = cards[j];
        cpt++;
      }
    }
    return result;
  }

  function createAndAssignCards(uint _num, string memory _img, uint _id, address _to) public onlyOwner {
    Collection collection = getCollection(_id);
    collection.createCard(_num, _img);
    collection.transfer(_to, collection.getTokenIdByCardNum(_num));
  }

  function createAndAssignCards(uint[] memory _num, string[] memory _img, uint _id, address _to) external onlyOwner {
    require(_num.length == _img.length, "Taille des tableaux non correspondante");
    for (uint i = 0; i <_num.length; i++) {
      createAndAssignCards(_num[i], _img[i], _id, _to);
    }
  }
}
