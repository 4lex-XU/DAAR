import React, { useState, useEffect } from 'react'
import { WalletProps, Card } from '../apiPokeTCG/types';
import Web3 from 'web3';
import MainABI from '@/abis/Main.json';
import { ethers } from "ethers";

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
          const valueInWei = ethers.utils.parseEther("0.3");
          let result = await window.contract.methods
              .openBooster(boosterName, accounts[0])
              .send({ from: accounts[0], value: valueInWei });
          
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
     <div style={{ padding: '20px' }}>
       <h2 style={{ fontSize: '1.8rem', marginBottom: '20px' }}>Liste des Boosters</h2>
       {boosters.length > 0 ? (
         <div style={{ display: 'flex', flexWrap: 'wrap', gap: '15px', marginBottom: '20px' }}>
                    {boosters.map((boosterName, index) => (

                      <div
                        key={index}
                        onClick={() => handleBoosterSelected(boosterName)}
                        style={{
                          textAlign: 'center',
                          position: 'relative',
                          width: 'auto',
                          cursor: 'pointer',
                          padding: '10px',
                          borderRadius: '8px',
                          backgroundColor: 'rgba(0, 0, 0, 0.5)',
                          boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.2)'
                        }}
                      >
                        <p
                           style={{
                             fontSize: '1rem',
                             fontWeight: 'bold',
                             color: 'white'
                           }}
                         >
                           Booster : {boosterName}
                         </p>
                        <div
                          style={{
                            position: 'absolute',
                            top: '0',
                            left: '0',
                            right: '0',
                            bottom: '0',
                            backgroundColor: 'rgba(0, 0, 0, 0.3)',
                            borderRadius: '8px',
                            zIndex: '-1'
                          }}
                        ></div>

                        <img src="../../public/booster.png" alt="Booster" width="200" height="auto" />

                      </div>
                    ))}
                  </div>
       ) : (
         <p style={{ fontSize: '1.2rem', color: '#fff' }}>Aucun booster disponible.</p>
       )}

       {receivedCards.length > 0 && (
         <div style={{ marginTop: '30px', textAlign: 'left' }}>
           <h3 style={{ fontSize: '1.5rem', marginBottom: '15px', color: '#333' }}>Cartes reçues :</h3>
           <ul style={{ listStyleType: 'none', paddingLeft: '0' }}>
             {receivedCards.map((card, idx) => (
               <li
                 key={idx}
                 style={{
                   backgroundColor: '#f5f5f5',
                   padding: '15px',
                   borderRadius: '8px',
                   marginBottom: '15px',
                   boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.1)'
                 }}
               >
                 <strong>Numéro:</strong> {card.num} <br />
                 <strong>Image:</strong> <img src={card.img} alt="Card Image" width="50" style={{ borderRadius: '5px' }} /> <br />
                 <strong>Collection:</strong> {card.nameCollection} <br />
                 <strong>Quantité:</strong> {card.cardCountCollection}
               </li>
             ))}
           </ul>
         </div>
       )}
     </div>
   );


}

export default Booster;
