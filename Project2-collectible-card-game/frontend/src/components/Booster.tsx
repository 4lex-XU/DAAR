import React, { useState, useEffect } from 'react'
import { WalletProps, Card } from '../apiPokeTCG/types';
import Web3 from 'web3';
import MainABI from '@/abis/Main.json';

const Booster: React.FC<WalletProps> = ({wallet}) => {
    
    const [boosters, setBoostersValue] = useState<string[]>([])
    const [receivedCards, setReceivedCards] = useState<Card[]>([]);

    useEffect(() => {
      if (wallet?.contract && wallet?.details.account) {
          getAllBoosters();
      }
    },[wallet])

    const getAllBoosters = async () => {
      if (!wallet.contract || !wallet.details.account) return;
      try {
          const keys = await wallet.contract.getAllKey();
          setBoostersValue(keys);
      } catch (error) {
          console.error("Erreur lors de la récupération des boosters :", error);
      }
    };

    async function getAccount() {
      let accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
      return accounts
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
      let address = "0x5FbDB2315678afecb367f032d93F642f64180aa3"
      return await new window.web3.eth.Contract(MainABI , address);
    }

    const openBooster = async (boosterName: string) => {
      if (!boosterName) {
        alert("Veuillez entrer un nom.");
        return;
      }

      let accounts = await getAccount();
      await loadWeb3();
      window.contract = await loadContract();
      
      try {
          let result = await window.contract.methods
              .openBooster(boosterName, accounts[0])
              .send({ from: accounts[0] });
          
          if (result.events && result.events.BoosterOpened) {
            const boosterOpenedEvent = result.events.BoosterOpened;
            console.log("Événement BoosterOpened détecté :", boosterOpenedEvent);
            const cardsReceived = boosterOpenedEvent.returnValues.cards;
            setReceivedCards(cardsReceived);
            console.log("Cartes reçues:", cardsReceived);
          } else {
            console.warn("L'événement BoosterOpened n'a pas été détecté.");
          }
          
          getAllBoosters();
          console.log(`Booster ${boosterName} ouvert avec succès !`);
      } catch (error) {
          console.error("Erreur lors de l'ouverture du booster :", error);
      }
    };

    const handleBoosterSelected = (booster: string) => {
      console.log("Booster sélectionné :", booster);
      openBooster(booster);
    };

    return (
      <div>
        <h2>Liste des Boosters</h2>
        {boosters.length > 0 ? (
          <div>
            {boosters.map((boosterName, index) => (
              <button
                key={index}
                onClick={() => handleBoosterSelected(boosterName)}
                style={{ margin: '10px', padding: '10px' }}
              >
                Ouvrir Booster : {boosterName}
              </button>
            ))}
          </div>
        ) : (
          <p>Aucun booster disponible.</p>
        )}
    
        {receivedCards.length > 0 ? (
          <div>
            <h3>Cartes reçues :</h3>
            <ul>
              {receivedCards.map((card, idx) => (
                <li key={idx}>
                  <strong>Numéro:</strong> {card.num} <br />
                  <strong>Image:</strong> <img src={card.img} alt="Card Image" width="50" /> <br />
                  <strong>Collection:</strong> {card.nameCollection} <br />
                  <strong>Quantité:</strong> {card.cardCountCollection}
                </li>
              ))}
            </ul>
          </div>
        ) : (
          <p>Aucune cartes disponible</p>
        )}
      </div>
    );
}

export default Booster;
