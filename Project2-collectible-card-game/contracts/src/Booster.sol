// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.10;

import "./Struct.sol";
import "./Ownable.sol";

contract Booster is Ownable {

    event NewBoosterCard(uint cardId, string num, string img);

    string public name;
    uint public max = 3;
    Card[] public boosterCards;

    constructor(address initialOwner, string memory _name) Ownable(initialOwner) {
        name = _name;
    }

    function createBoosterCard(string memory _num, string memory _img, string memory _name, uint _cardCount) onlyOwner external {
        require(boosterCards.length <= 3, unicode"Taille max atteint du booster");
        boosterCards.push(Card(_num, _img, _name, _cardCount));
        uint id = boosterCards.length - 1;
        emit NewBoosterCard(id, _num, _img);
    }
}
