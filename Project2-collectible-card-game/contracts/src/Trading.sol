// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.10;

import "./Struct.sol";
import {Collection} from "./Collection.sol";
import "./Ownable.sol";

contract Trading is Ownable{

    Collection public collection;
    uint256 public saleCounter = 0;
    uint256 public exchangeCounter = 0;
    mapping(uint256 => Sale) public sales;

    event CardListed(uint256 indexed saleId, string indexed cardId, address indexed seller);
    event CardSold(uint256 indexed saleId, string indexed cardId, address indexed buyer);
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

    function listCardForSale(string memory _cardId) external {
        sales[saleCounter] = Sale(saleCounter, _cardId, msg.sender, true);
        emit CardListed(saleCounter, _cardId, msg.sender);
        saleCounter++;
    }

    function buyCard(uint256 _saleId) external payable {
        Sale storage sale = sales[_saleId];
        require(sale.isAvailable, unicode"La carte n'est plus disponible.");

        //payable(sale.seller).transfer(msg.value);
        collection.transferForMarket(sale.seller, msg.sender, collection.getTokenIdByCardNum(sale.cardId));

        sale.isAvailable = false;
        emit CardSold(_saleId, sale.cardId, msg.sender);
    }

    function removeSale(uint256 _saleId) external {
        Sale storage sale = sales[_saleId];
        require(sale.seller == msg.sender, unicode"Vous n'êtes pas le propriétaire de cette vente.");
        sale.isAvailable = false;
    }

    function getAllSale() external view returns (uint256[] memory, Card[] memory) {
        uint256 availableSaleCount = 0;
        for (uint256 i = 0; i < saleCounter; i++) {
            if (sales[i].isAvailable) {
                availableSaleCount++;
            }
        }

        uint256[] memory saleIds = new uint256[](availableSaleCount);
        Card[] memory cardsSale = new Card[](availableSaleCount);
        uint256 index = 0;
        for (uint256 i = 0; i < saleCounter; i++) {
            if (sales[i].isAvailable) {
                saleIds[index] = sales[i].saleId;
                string memory cardSaleId = sales[i].cardId;
                (string memory name, string memory img, string memory nameCollection, uint256 cardCountCollection) = collection.cards(collection.getTokenIdByCardNum(cardSaleId));
                cardsSale[index] = Card(name, img, nameCollection, cardCountCollection);
                index++;
            }
        }
        return (saleIds, cardsSale);
    }
}
