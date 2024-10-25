import React, { useState, useEffect } from 'react'
import { WalletProps, Card } from '../apiPokeTCG/types';
import Web3 from 'web3';
import { ethers } from 'ethers';

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
      let abi = [
        {
          "inputs": [],
          "stateMutability": "nonpayable",
          "type": "constructor"
        },
        {
          "anonymous": false,
          "inputs": [
            {
              "indexed": true,
              "internalType": "address",
              "name": "user",
              "type": "address"
            },
            {
              "components": [
                {
                  "internalType": "string",
                  "name": "num",
                  "type": "string"
                },
                {
                  "internalType": "string",
                  "name": "img",
                  "type": "string"
                },
                {
                  "internalType": "string",
                  "name": "nameCollection",
                  "type": "string"
                },
                {
                  "internalType": "uint256",
                  "name": "cardCountCollection",
                  "type": "uint256"
                }
              ],
              "indexed": false,
              "internalType": "struct Card[]",
              "name": "cards",
              "type": "tuple[]"
            }
          ],
          "name": "BoosterOpened",
          "type": "event"
        },
        {
          "anonymous": false,
          "inputs": [
            {
              "indexed": false,
              "internalType": "uint256",
              "name": "boosterId",
              "type": "uint256"
            },
            {
              "indexed": false,
              "internalType": "string",
              "name": "name",
              "type": "string"
            }
          ],
          "name": "NewBooster",
          "type": "event"
        },
        {
          "anonymous": false,
          "inputs": [
            {
              "indexed": false,
              "internalType": "uint256",
              "name": "collectionId",
              "type": "uint256"
            },
            {
              "indexed": false,
              "internalType": "string",
              "name": "name",
              "type": "string"
            },
            {
              "indexed": false,
              "internalType": "uint256",
              "name": "cardCount",
              "type": "uint256"
            }
          ],
          "name": "NewCollection",
          "type": "event"
        },
        {
          "anonymous": false,
          "inputs": [
            {
              "indexed": true,
              "internalType": "address",
              "name": "previousOwner",
              "type": "address"
            },
            {
              "indexed": true,
              "internalType": "address",
              "name": "newOwner",
              "type": "address"
            }
          ],
          "name": "OwnershipTransferred",
          "type": "event"
        },
        {
          "inputs": [
            {
              "internalType": "string[]",
              "name": "_num",
              "type": "string[]"
            },
            {
              "internalType": "string[]",
              "name": "_img",
              "type": "string[]"
            },
            {
              "internalType": "string",
              "name": "_name",
              "type": "string"
            },
            {
              "internalType": "uint256",
              "name": "_cardCount",
              "type": "uint256"
            },
            {
              "internalType": "address",
              "name": "_to",
              "type": "address"
            }
          ],
          "name": "createAndAssignCards",
          "outputs": [],
          "stateMutability": "nonpayable",
          "type": "function"
        },
        {
          "inputs": [
            {
              "internalType": "string[]",
              "name": "_num",
              "type": "string[]"
            },
            {
              "internalType": "string[]",
              "name": "_img",
              "type": "string[]"
            },
            {
              "internalType": "string",
              "name": "_name",
              "type": "string"
            },
            {
              "internalType": "string",
              "name": "_nameCollection",
              "type": "string"
            },
            {
              "internalType": "uint256",
              "name": "_cardCount",
              "type": "uint256"
            }
          ],
          "name": "createBoosterCards",
          "outputs": [],
          "stateMutability": "nonpayable",
          "type": "function"
        },
        {
          "inputs": [],
          "name": "getAllKey",
          "outputs": [
            {
              "internalType": "string[]",
              "name": "",
              "type": "string[]"
            }
          ],
          "stateMutability": "view",
          "type": "function"
        },
        {
          "inputs": [
            {
              "internalType": "address",
              "name": "_owner",
              "type": "address"
            }
          ],
          "name": "getCardsByOwner",
          "outputs": [
            {
              "components": [
                {
                  "internalType": "string",
                  "name": "num",
                  "type": "string"
                },
                {
                  "internalType": "string",
                  "name": "img",
                  "type": "string"
                },
                {
                  "internalType": "string",
                  "name": "nameCollection",
                  "type": "string"
                },
                {
                  "internalType": "uint256",
                  "name": "cardCountCollection",
                  "type": "uint256"
                }
              ],
              "internalType": "struct Card[]",
              "name": "",
              "type": "tuple[]"
            }
          ],
          "stateMutability": "view",
          "type": "function"
        },
        {
          "inputs": [
            {
              "internalType": "string",
              "name": "name",
              "type": "string"
            },
            {
              "internalType": "address",
              "name": "_to",
              "type": "address"
            }
          ],
          "name": "openBooster",
          "outputs": [],
          "stateMutability": "nonpayable",
          "type": "function"
        },
        {
          "inputs": [],
          "name": "owner",
          "outputs": [
            {
              "internalType": "address",
              "name": "",
              "type": "address"
            }
          ],
          "stateMutability": "view",
          "type": "function"
        },
        {
          "inputs": [
            {
              "internalType": "address",
              "name": "newOwner",
              "type": "address"
            }
          ],
          "name": "transferOwnership",
          "outputs": [],
          "stateMutability": "nonpayable",
          "type": "function"
        }
      ] 
      let address = "0x5FbDB2315678afecb367f032d93F642f64180aa3"
      return await new window.web3.eth.Contract(abi, address);
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
