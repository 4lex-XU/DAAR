// SPDX-License-Identifier: MIT
pragma solidity ^0.8;

import "./ERC721.sol";
import "./Struct.sol";
import "./Ownable.sol";

contract Collection is Ownable, ERC721{

  event NewCard(uint cardId, string num, string img);

  string public name;
  uint public cardCount;

  Card[] public cards;

  mapping (uint => address) cardApprovals;
  // indique les propriétaire de chaque cartes
  mapping (uint => address) public cardToOwner;
  // indique le nombre de carte que possede un utilisateur
  mapping (address => uint) ownerCardCount;

  modifier onlyOwnerOf(uint _cardId) {
    require(msg.sender == cardToOwner[_cardId]);
    _;
  }

  constructor(string memory _name, uint _cardCount) {
    name = _name;
    cardCount = _cardCount;
  }

  function createCard(string memory _num, string memory _img) external {
    require(cards.length < cardCount, "Nombre de cartes maximum atteint");
    cards.push(Card(_num, _img));
    uint id = cards.length - 1;
    cardToOwner[id] = msg.sender;
    ownerCardCount[msg.sender]++;
    emit NewCard(id, _num, _img);
  }

  function getCards(address _owner) external view returns (Card[] memory) {
    require(cards.length > 0, "Aucune carte existante");
    Card[] memory result = new Card[](ownerCardCount[_owner]);
    uint cpt = 0;
    for (uint i = 0; i < cards.length; i++) {
      if (cardToOwner[i] == _owner) {
        result[cpt] = cards[i];
        cpt++;
      }
    }
    return result;
  }

  function getTokenIdByCardNum(string memory _num) external view returns (uint tokenId) {
    require(cards.length > 0, "Aucune carte existante");
    for (uint i = 0; i < cards.length; i++) {
      if (keccak256(abi.encodePacked(cards[i].num)) == keccak256(abi.encodePacked(_num))) {

        return i;
      }
    }
    revert(unicode"Identifiant unique du numero correspondant non trouvé");
  }

  function balanceOf(address _owner) public view override returns (uint256 _balance) {
    return ownerCardCount[_owner];
  }

  function ownerOf(uint256 _tokenId) public view override returns (address _owner) {
    return cardToOwner[_tokenId];
  }

  function _transfer(address _from, address _to, uint256 _tokenId) internal {
    ownerCardCount[_to]++;
    ownerCardCount[_from]--;
    cardToOwner[_tokenId] = _to;
    emit Transfer(_from, _to, _tokenId);
  }

  function transfer(address _to, uint256 _tokenId) public override onlyOwnerOf(_tokenId) {
    _transfer(msg.sender, _to, _tokenId);
  }

  function approve(address _to, uint256 _tokenId) public override onlyOwnerOf(_tokenId) {
    cardApprovals[_tokenId] = _to;
    emit Approval(msg.sender, _to, _tokenId);
  }

  function takeOwnership(uint256 _tokenId) public override {
    require(cardApprovals[_tokenId] == msg.sender);
    _transfer(ownerOf(_tokenId), msg.sender, _tokenId);
  }
}
