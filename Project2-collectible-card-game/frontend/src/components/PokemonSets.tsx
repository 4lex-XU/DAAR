import React, { useState, useEffect } from 'react';
import { getPokemonSets, getCardsFromSet } from '../apiPokeTCG/apiService';
import { PokemonSet, PokemonCard } from '../apiPokeTCG/types';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Card from 'react-bootstrap/Card';
import Button from 'react-bootstrap/Button';
import Web3 from 'web3'
import MainABI from '@/abis/Main.json';

const PokemonSets: React.FC = () => {
  const [sets, setSets] = useState<PokemonSet[]>([]);
  const [selectedSetId, setSelectedSetId] = useState<string | null>(null);
  const [selectedCards, setSelectedCards] = useState<PokemonCard[]>([]);
  const [cards, setCards] = useState<PokemonCard[]>([]);
  const [loadingSets, setLoadingSets] = useState<boolean>(true);
  const [loadingCards, setLoadingCards] = useState<boolean>(false);
  const [error, setError] = useState<Error | null>(null);
  const [userReceive, setUserReceive] = useState("");
  const [name, setName] = useState("");

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

  const handleSetClick = async (setId: string) => {
    setSelectedSetId(setId);
    setLoadingCards(true);
    try {
      const data = await getCardsFromSet(setId);
      setCards(data);
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
    );
  };

  const isCardSelected = (cardId: string) => selectedCards.some(card => card.id === cardId);

  const selectedSet = sets.find((set) => set.id === selectedSetId);

  if (loadingSets) return <p>Chargement des sets...</p>;
  if (error) return <p>Erreur : {error.message}</p>;

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

  const mintCards = async () => {
    if (!userReceive) {
      alert("Veuillez entrer une adresse valide.");
      return;
    }

    let accounts = await getAccount()
    await loadWeb3();
    window.contract = await loadContract();

    const idAttribute = selectedCards.map(card => card.id)
    const imgAttribute = selectedCards.map(card => card.images.small)

    if (!selectedSet?.name || !selectedSet?.total) {
      throw new Error('Arguments manquants pour createAndAssignCards')
    }

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

  const handleUserValue = (event) => {
    setUserReceive(event.target.value);
  };

  // BOOSTER
  const createBooster = async () => {
    if (!name) {
      alert("Veuillez entrer un nom valide.");
      return;
    }

    let accounts = await getAccount()
    await loadWeb3();
    window.contract = await loadContract();

    const idAttribute = selectedCards.map(card => card.id)
    const imgAttribute = selectedCards.map(card => card.images.small)

    try {
        await window.contract.methods
        .createBoosterCards(
            idAttribute,
            imgAttribute,
            name,
            selectedSet?.name,
            selectedSet?.total,
        )
        .send({ from: accounts[0] });
        console.log(`Booster créé avec succès !`);
    } catch (error) {
        console.error("Erreur lors de la création du booster :", error);
    }
  };

  const handleNameValue = (event) => {
    setName(event.target.value);
  };

  return (
    <div>
      {selectedSetId === null ? (
        <Container>
          <Row>
            {sets.map((set) => (
              <Col key={set.id} md={4} lg={3}>
                <Card
                  onClick={() => handleSetClick(set.id)}
                  style={{
                    cursor: 'pointer',
                    width: '250px',
                    height: '350px',
                    position: 'relative',
                    backgroundColor: 'transparent',
                    border: '3px solid white',
                    textAlign: 'center',
                    marginBottom: '20px',
                    borderRadius: '10px',
                    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
                    overflow: 'hidden'
                  }}
                >
                  {/* Pseudo-élément pour le fond sombre */}
                  <div
                    style={{
                      position: 'absolute',
                      top: 0,
                      left: 0,
                      width: '100%',
                      height: '100%',
                      backgroundColor: 'rgba(0, 0, 0, 0.5)',
                      zIndex: 1
                    }}
                  ></div>

                  {/* Contenu de la carte (au-dessus du fond sombre) */}
                  <div style={{ position: 'relative', zIndex: 2 }}>
                    <Card.Img
                      variant="top"
                      src={set.images.logo}
                      alt={`${set.name} logo`}
                      style={{
                        width: '100%',
                        height: '200px',
                        objectFit: 'contain'
                      }}
                    />
                    <Card.Body>
                      <Card.Title style={{ color: 'white' }}>{set.name}</Card.Title>
                      <Card.Text style={{ color: 'white' }}>
                        {set.series} - {set.releaseDate}
                      </Card.Text>
                    </Card.Body>
                  </div>
                </Card>
              </Col>
            ))}
          </Row>
        </Container>
      ) : (
        <div>
          <Button onClick={() => setSelectedSetId(null)}>Retour aux sets</Button>
          {loadingCards ? (
            <p>Chargement des cartes...</p>
          ) : (
            <div
                style={{
                  display: 'inline-block',
                  padding: '20px',
                  backgroundColor: 'rgba(0, 0, 0, 0.5)', // Fond sombre avec transparence
                  borderRadius: '10px',
                }}
              >

              {selectedSet && (
                <div style={{ textAlign: 'center' }}>
                  <img src={selectedSet.images.logo} alt={selectedSet.name} width="200" />
                </div>
              )}
              <br/>
              {selectedCards.length > 0 && (
                  <div
                    style={{
                      textAlign: 'left',
                      padding: '20px',
                      borderRadius: '10px',
                      backgroundColor: 'rgba(0, 0, 0, 0.5)',
                      boxShadow: '0px 4px 12px rgba(0, 0, 0, 0.1)',
                      width: '80%',
                      margin: '0 auto'
                    }}
                  >
                    <p style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>Cartes sélectionnées : {selectedCards.length}</p>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '15px' }}>
                      <input
                        type="text"
                        onChange={handleUserValue}
                        placeholder="Entrez l'adresse du compte"
                        style={{
                          padding: '8px',
                          width: '55%',
                          borderRadius: '5px',
                          border: '1px solid #ced4da',
                          fontSize: '1rem'
                        }}
                      />
                      <Button onClick={mintCards} variant="primary" style={{ fontSize: '0.9rem', padding: '8px 12px' }}>
                        Créer et assigner
                      </Button>
                      <Button
                        variant="warning"
                        style={{ fontSize: '0.9rem', padding: '8px 12px' }}
                        onClick={() => setSelectedCards([])}
                      >
                        Tout désélectionner
                      </Button>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <input
                        type="text"
                        onChange={handleNameValue}
                        placeholder="Entrez le nom du booster"
                        style={{
                          padding: '8px',
                          width: '55%',
                          borderRadius: '5px',
                          border: '1px solid #ced4da',
                          fontSize: '1rem'
                        }}
                      />
                      <Button
                        onClick={createBooster}
                        variant="success"
                        style={{ fontSize: '0.9rem', padding: '8px 12px' }}
                      >
                        Créer un booster
                      </Button>
                    </div>
                  </div>

                )}
                <br/>
              <Container>
                <Row>
                  {cards.map((card) => (
                    <Col key={card.id}>
                      <div
                        onClick={() => handleCardSelect(card)}
                        style={{
                          border: isCardSelected(card.id) ? '2px solid red' : 'none',
                          cursor: 'pointer',
                          marginBottom: '20px'
                        }}
                      >
                        <img src={card.images.small} alt={card.name} />
                      </div>
                    </Col>
                  ))}
                </Row>
              </Container>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default PokemonSets;
