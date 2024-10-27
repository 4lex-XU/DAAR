import React, { useEffect, useState } from 'react';
import { Sale } from '../apiPokeTCG/types';
import Web3 from 'web3';
import MainABI from '@/abis/Main.json';
import { ethers } from "ethers";

const MarketPlace: React.FC = () => {
    const [contract, setContract] = useState(null);
    const [account, setAccount] = useState("");
    const [cardsSale, setCardsSale] = useState<Sale[]>([]);

    useEffect(() => {
        async function init() {
            await loadWeb3();
            const accounts = await getAccount();
            setAccount(accounts[0]);
            const tradingContract = await loadContract();
            setContract(tradingContract);
        }
        init();
    }, []);

    useEffect(() => {
        if (contract) {
            loadCardsForSale();
        }
    }, [contract]);

    async function getAccount() {
        let accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
        return accounts;
    }

    async function loadWeb3() {
        if (window.ethereum) {
            window.web3 = new Web3(window.ethereum);
            await window.ethereum.request({ method: 'eth_requestAccounts' });
        } else {
            console.error("Veuillez installer MetaMask !");
        }
    }

    async function loadContract() {
        const address = "0x5FbDB2315678afecb367f032d93F642f64180aa3";
        return new window.web3.eth.Contract(MainABI, address);
    }

    async function loadCardsForSale() {
        try {
            const result = await contract.methods.getAllSales().call();

            console.log('Résultat de getAllSales:', result);

            const saleIds = result[0];
            const cards = result[1];

            const formattedSales: Sale[] = saleIds.map((id: number, index: number) => ({
                saleId: id,
                card: cards[index],
            }));
            setCardsSale(formattedSales);
        } catch (error) {
            console.error('Erreur lors de la récupération des cartes :', error);
        }
    };

    async function buyCard(collectionName: string, saleId: number) {
        try {
            const valueInWei = ethers.utils.parseEther("0.1");
            await contract.methods
                .buyCard(collectionName, saleId) 
                .send({ from: account, value: valueInWei });

            console.log("Achat de la carte réussi !");
            loadCardsForSale();
        } catch (error) {
            console.error("Erreur lors de l'achat de la carte :", error);
        }
    }

    async function removeSale(collectionName: string, saleId: number) {
        try {
            await contract.methods
                .removeSale(collectionName, saleId) 
                .send({ from: account });

            console.log("Suppression de la carte du marketplace réussie !");
            loadCardsForSale(); 
        } catch (error) {
            console.error("Erreur lors de la suppression de la vente :", error);
        }
    }

    return (
        <div>
            <h2>MarketPlace</h2>
            <h3>Cartes en Vente</h3>
            <ul>
                {cardsSale.map((sale, index) => (
                    <li key={index}>
                        <h3>{sale.card.num}</h3>
                        <img src={sale.card.img} alt={sale.card.num} style={{ width: '100px' }} />
                        <button onClick={() => buyCard(sale.card.nameCollection, sale.saleId)}>Acheter</button>
                        <button onClick={() => removeSale(sale.card.nameCollection, sale.saleId)}>Retirer de la vente</button>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default MarketPlace;
