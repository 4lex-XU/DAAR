import React, { useState, useEffect } from 'react';
import { getPokemonSets, getCardsFromSet } from '../apiPokeTCG/apiService';
import { PokemonSet, PokemonCard } from '../apiPokeTCG/types';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Card from 'react-bootstrap/Card';
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

  const mintCards = () => {
    // Code pour minter les cartes
  };

  const handleValue = (event) => {
    setUserReceive(event.target.value);
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
                      backgroundColor: 'rgba(0, 0, 0, 0.5)', // Fond sombre avec opacité
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
            <div>
              {selectedSet && (
                <div style={{ textAlign: 'center' }}>
                  <img src={selectedSet.images.logo} alt={selectedSet.name} width="200" />
                </div>
              )}
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
