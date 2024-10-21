import React, { useState, useEffect } from 'react';
import { getPokemonSets, getCardsFromSet } from '../apiPokeTCG/apiService';
import { PokemonSet, PokemonCard } from '../apiPokeTCG/types';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import Web3 from 'web3'

const PokemonSets: React.FC = () => {
  const [sets, setSets] = useState<PokemonSet[]>([]);
  const [selectedSetId, setSelectedSetId] = useState<string | null>(null);
  const [selectedCards, setSelectedCards] = useState<PokemonCard[]>([]);
  const [cards, setCards] = useState<PokemonCard[]>([]);
  const [loadingSets, setLoadingSets] = useState<boolean>(true);
  const [loadingCards, setLoadingCards] = useState<boolean>(false);
  const [error, setError] = useState<Error | null>(null);
  const [userReceive, setUserReceive] = useState("");

  // Charger les sets de Pokémon au chargement initial
  useEffect(() => {
    async function fetchSets() {
      try {
        const data = await getPokemonSets();
        setSets(data);
      } catch (error) {
        setError(error as Error);
      } finally {
        setLoadingSets(false);
      }
    }
    fetchSets();
  }, []);

  // Fonction pour récupérer et afficher les cartes d'un set sélectionné
  const handleSetClick = async (setId: string) => {
    setSelectedSetId(setId);  // Stocke l'ID du set sélectionné
    setLoadingCards(true);
    try {
      const data = await getCardsFromSet(setId);
      setCards(data);  // Met à jour l'état des cartes récupérées
    } catch (error) {
      setError(error as Error);
    } finally {
      setLoadingCards(false);
    }
  };

  const handleCardSelect = (card: PokemonCard) => {
    setSelectedCards(prevSelectedCards =>
      prevSelectedCards.find(selected => selected.id === card.id)
        ? prevSelectedCards.filter(selected => selected.id !== card.id)
        : [...prevSelectedCards, card]
    )
  }

  const isCardSelected = (cardId: string) => selectedCards.some(card => card.id === cardId);

  // Trouver le set sélectionné dans la liste
  const selectedSet = sets.find((set) => set.id === selectedSetId);

  if (loadingSets) return <p>Chargement des sets...</p>;
  if (error) return <p>Erreur : {error.message}</p>;

  const mintCards = () => {

    if (!userReceive) {
      alert("Veuillez entrer une adresse valide.");
      return;
    }

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
            "stateMutability": "payable",
            "type": "fallback"
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
          },
          {
            "stateMutability": "payable",
            "type": "receive"
          }
        ] 
        let address = "0x5FbDB2315678afecb367f032d93F642f64180aa3"
        return await new window.web3.eth.Contract(abi, address);
    }

    const mint = async () => {
      let accounts = await getAccount()
      await loadWeb3();
      window.contract = await loadContract();

      const idAttribute = selectedCards.map(card => card.id)
      const imgAttribute = selectedCards.map(card => card.images.small)

      if (!selectedSet?.name || !selectedSet?.total) {
        throw new Error('Missing required information for createAndAssignCards')
      }

      console.log("id : ", idAttribute)
      console.log("img :", imgAttribute)
      console.log("name :", selectedSet.name)
      console.log("total :", selectedSet.total)
      console.log("addr :", userReceive)

      try {
        let result = await window.contract.methods
        .createAndAssignCards(
          idAttribute,
          imgAttribute,
          selectedSet?.name,
          selectedSet?.total,
          userReceive
        )
        .send({ from: accounts[0] })

        console.log("Cartes mintées avec succès :", result)
      } catch (error) {
        console.error("Erreur lors du mint des cartes", error)
      }
    }
    mint();
  }

  const handleValue = (event) => {
    setUserReceive(event.target.value);
  }

  return (
    <div>
      {/* Liste des sets */}
      {selectedSetId === null ? (
        <div>

          <ul>
            {sets.map((set) => (
              <li key={set.id}>
                <img src={set.images.logo} alt={`${set.name} logo`} width="50" />
                <strong>{set.name}</strong> ({set.series}) - {set.releaseDate}
                <Button
                  onClick={() => handleSetClick(set.id)}
                  style={{ marginLeft: '10px' }}
                >
                  Voir les cartes
                </Button>
              </li>
            ))}
          </ul>
        </div>
      ) : (
        <div>
          <Button onClick={() => setSelectedSetId(null)}>Retour aux sets</Button>
          {loadingCards ? (
            <p>Chargement des cartes...</p>
          ) : (
            <div>
              {/* Affichage de l'image du set sélectionné */}
              {selectedSet && (
                <div style={{ textAlign: 'center' }}>
                  <img src={selectedSet.images.logo} alt={selectedSet.name} width="200" />
                </div>
              )}
              {/* Affichage des cartes en grille */}
              <Container>
                <Row>
                  {cards.map((card) => (
                    <Col key={card.id}>
                      <div
                        onClick={() => handleCardSelect(card)}
                        style={{
                          border: isCardSelected(card.id) ? '2px solid blue' : 'none',
                          cursor: 'pointer'
                        }}
                      >
                        <img src={card.images.small} alt={card.name} />
                      </div>
                    </Col>
                  ))}
                </Row>
              </Container>
              {selectedCards.length > 0 && (
                <div style={{ marginTop: '20px' }}>
                  <p>Cartes sélectionnées : {selectedCards.length}</p>
                  <input type="text" onChange={handleValue} placeholder="Entrer une adresse"></input>
                  <Button onClick={mintCards}>
                    Créer et assigner les cartes sélectionnées
                  </Button>
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default PokemonSets;
