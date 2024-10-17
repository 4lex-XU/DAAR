import React, { useState, useEffect } from 'react';
import { getPokemonSets, getCardsFromSet } from '../apiPokeTCG/apiService';
import { PokemonSet, PokemonCard } from '../apiPokeTCG/types';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';

const PokemonSets: React.FC = () => {
  const [sets, setSets] = useState<PokemonSet[]>([]);
  const [selectedSetId, setSelectedSetId] = useState<string | null>(null);
  const [cards, setCards] = useState<PokemonCard[]>([]);
  const [loadingSets, setLoadingSets] = useState<boolean>(true);
  const [loadingCards, setLoadingCards] = useState<boolean>(false);
  const [error, setError] = useState<Error | null>(null);

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

  // Trouver le set sélectionné dans la liste
  const selectedSet = sets.find((set) => set.id === selectedSetId);

  if (loadingSets) return <p>Chargement des sets...</p>;
  if (error) return <p>Erreur : {error.message}</p>;

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
                      <img src={card.images.small} alt={card.name} />
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
