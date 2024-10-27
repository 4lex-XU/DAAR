import React, { useEffect, useState } from 'react';
import { Card, Sale } from '../apiPokeTCG/types';
import Web3 from 'web3';
import MainABI from '@/abis/Main.json';
import { ethers } from "ethers";
import Button from 'react-bootstrap/Button';

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

            const saleIds = result[0];
            const cards = result[1];

            const formattedSales: Sale[] = cards.map((card: Card, index: number) => ({
                saleId: saleIds[index],
                card: {
                    num: card.num,
                    img: card.img,
                    nameCollection: card.nameCollection,
                    cardCountCollection: card.cardCountCollection,
                },
            }));

            setCardsSale(formattedSales);
            console.log('Résultat de getAllSales:', formattedSales);
        } catch (error) {
            console.error('Erreur lors de la récupération des cartes :', error);
        }
    }

    async function buyCard(collectionName: string, cardId: string) {
        try {
            const valueInWei = ethers.utils.parseEther("0.1");
            await contract.methods
                .buyCard(collectionName, cardId, account!)
                .send({ from: account, value: valueInWei });

            console.log("Achat de la carte réussi !");
            loadCardsForSale();
        } catch (error) {
            console.error("Erreur lors de l'achat de la carte :", error);
        }
    }

    async function removeSale(collectionName: string, cardId: string) {
        try {
            await contract.methods
                .removeSale(collectionName, cardId, account)
                .send({ from: account });

            console.log("Suppression de la carte du marketplace réussie !");
            loadCardsForSale(); 
        } catch (error) {
            console.error("Erreur lors de la suppression de la vente :", error);
        }
    }

    return (
      <div style={{ padding: '20px'}}>
        <h2 style={{ fontSize: '1.8rem', marginBottom: '20px', color: '#fff' }}>MarketPlace</h2>

        {cardsSale.length > 0 ? (
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px'}}>
            {cardsSale.map((sale, index) => (
              <div
                key={index}
                style={{
                  width: '200px',
                  padding: '15px',
                  borderRadius: '8px',
                  backgroundColor: 'rgba(0, 0, 0, 0.5)',
                  boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.1)',
                  textAlign: 'center'
                }}
              >
                <h3 style={{ fontSize: '1.2rem', color: '#fff' }}>{sale.card.num}</h3>
                <img
                  src={sale.card.img}
                  alt={sale.card.num}
                  style={{ width: '100px', height: 'auto', marginBottom: '10px', borderRadius: '8px' }}
                />
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: '10px', marginTop: '10px' }}>
                  <Button
                    onClick={() => buyCard(sale.card.nameCollection, sale.card.num)}
                    variant="success"
                    style={{ fontSize: '0.9rem', padding: '8px 12px', width: '100%' }}
                  >
                    Acheter
                  </Button>
                  <Button
                    onClick={() => removeSale(sale.card.nameCollection, sale.card.num)}
                    variant="danger"
                    style={{ fontSize: '0.9rem', padding: '8px 12px', width: '100%' }}
                  >
                    Retirer
                  </Button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p style={{ fontSize: '1.2rem', color: '#777' }}>Aucune carte en vente pour le moment.</p>
        )}
      </div>
    );

}

export default MarketPlace;
