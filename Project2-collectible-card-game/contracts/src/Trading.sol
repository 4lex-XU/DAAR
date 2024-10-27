// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.10;

import "./Struct.sol";
import {Collection} from "./Collection.sol";
import "./Ownable.sol";

contract Trading is Ownable{

    Collection public collection;
    uint256 public saleCounter = 0;
    uint256 public exchangeCounter = 0;
    mapping(string => Sale) public sales;
    string[] private saleNames;

    event CardListed(uint256 indexed saleId, string indexed cardId, address indexed seller);
    event CardSold(string indexed cardId, address indexed buyer);
    event CardExchanged(string indexed cardId1, string indexed cardId2, address indexed user1, address user2);

    event ExchangeProposed(uint256 indexed offerId, string offeredCardId, string requestedCardId, address indexed initiator);
    event ExchangeCompleted(uint256 indexed offerId, string offeredCardId, string requestedCardId, address indexed initiator, address indexed acceptor);
    event ExchangeCancelled(uint256 indexed offerId, address indexed initiator);

    constructor(address initialOwner, Collection initialCollection) Ownable(initialOwner) {
        collection = initialCollection;
    }

    modifier onlyCardOwner(string memory _cardId) {
        require(msg.sender == collection.ownerOf(collection.getTokenIdByCardNum(_cardId)), unicode"Vous n'êtes pas le propriétaire de cette carte.");
        _;
    }

    function listCardForSale(string memory _cardId, address sender) external {
        require(sales[_cardId].isAvailable == false, unicode"Trading de la carte deja effectué");
        sales[_cardId] = Sale(saleCounter, _cardId, sender, true);
        saleNames.push(_cardId);
        emit CardListed(saleCounter, _cardId, msg.sender);
        saleCounter++;
    }

    function buyCard(string memory _cardId, address sender) external payable {
        require(msg.sender != sales[_cardId].seller, unicode"L'emetteur ne peut pas acheter sa propre carte");
        Sale storage sale = sales[_cardId];
        require(sale.isAvailable, unicode"La carte n'est plus disponible.");

        //payable(sale.seller).transfer(msg.value);
        collection.transferForMarket(sale.seller, sender, _cardId);

        sale.isAvailable = false;
        emit CardSold(sale.cardId, msg.sender);
    }

    function removeSale(string memory _cardId) external {
        Sale storage sale = sales[_cardId];
        require(sale.seller == msg.sender, unicode"Vous n'êtes pas le propriétaire de cette vente.");
        sale.isAvailable = false;
    }

    function getAllSale() external view returns (uint256[] memory, Card[] memory) {
        uint256 availableSaleCount = 0;
        for (uint256 i = 0; i < saleCounter; i++) {
            if (sales[saleNames[i]].isAvailable == true) {
                availableSaleCount++;
            }
        }

        uint256[] memory saleIds = new uint256[](availableSaleCount);
        Card[] memory cardsSale = new Card[](availableSaleCount);
        uint256 index = 0;
        for (uint256 i = 0; i < saleCounter; i++) {
            if (sales[saleNames[i]].isAvailable == true) {
                saleIds[index] = sales[saleNames[i]].saleId;
                (string memory name, string memory img, string memory nameCollection, uint256 cardCountCollection) = collection.cards(collection.getTokenIdByCardNum(saleNames[i]));
                cardsSale[index] = Card(name, img, nameCollection, cardCountCollection);
                index++;
            }
        }
        return (saleIds, cardsSale);
    }
}
