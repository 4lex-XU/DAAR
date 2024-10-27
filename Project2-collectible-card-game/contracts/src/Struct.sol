// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.10;

struct Card {
    string num;
    string img;
    string nameCollection;
    uint cardCountCollection;
}

struct Sale {
    uint256 saleId;
    string cardId;
    address seller;
    bool isAvailable;
}
