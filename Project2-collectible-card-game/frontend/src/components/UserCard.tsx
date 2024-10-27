import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { WalletProps, Card } from '../apiPokeTCG/types';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import MainABI from '@/abis/Main.json';
import Web3 from 'web3';
import { Modal, Button } from 'react-bootstrap';

const UserCard: React.FC<WalletProps> = ({ wallet }) => {
  const { account } = useParams();
  const [cards, setCardsValue] = useState<Card[]>([]);
  const [selectedCard, setSelectedCard] = useState<Card>();
  const [showModal, setShowModal] = useState(false);

  const handleListCardForSale = async () => {
    if (!selectedCard) return;

    try {
      await loadWeb3();
      const contract = await loadContract();

        await contract.methods
            .listCardForSale(selectedCard?.nameCollection, selectedCard?.num)
            .send({ from: account });
            
        alert('Carte mise en vente avec succès !');
        setShowModal(false);
    } catch (error) {
      console.error("Erreur lors de la mise en vente de la carte :", error);
    }
  };

  const handleShowModal = (card: Card) => {
    setSelectedCard(card);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setSelectedCard(undefined);
    setShowModal(false);
  };

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

  useEffect(() => {
    getCardsByOwner();
  }, []);

  async function getCardsByOwner() {
      if (wallet?.contract && wallet?.details.account) {
          try {
          const data = await wallet.contract.getCardsByOwner(account!)
          const formattedCards = data.map((tuple: any) => ({
              num: tuple.num || tuple[0],
              img: tuple.img || tuple[1],
              nameCollection: tuple.nameCollection || tuple[2],
              cardCountCollection: tuple.cardCountCollection || tuple[3]
          }));
          setCardsValue(formattedCards);
          console.log("Cartes du joueur :", formattedCards);
          console.log('getCardsByOwner: '+wallet.details.account+' reussie !')
          } catch (err) {
          console.log('Erreur lors du getCardsByOwner.')
          console.error(err)
          }
      }
  }

  return (
    <div>
      <h1>Ma Collection</h1>
      {cards.length > 0 ? (
        <Container>
          <Row>
            {cards.map((card, index) => (
              <Col key={index} xs={12} sm={6} md={4} lg={3}>
                <div className="card-item" style={{ textAlign: 'center', marginBottom: '20px' }}>
                  <img src={card.img} alt={`Carte ${card.num}`} style={{ width: '100%', borderRadius: '8px' }} />
                  <Button
                    variant="primary"
                    onClick={() => handleShowModal(card)}
                    style={{ marginTop: '10px' }}
                  >
                    Mettre en vente
                  </Button>
                </div>
              </Col>
            ))}
          </Row>
        </Container>
      ) : (
        <p>Aucune carte trouvée</p>
      )}

      {/* Modal de confirmation */}
      <Modal show={showModal} onHide={handleCloseModal} centered>
        <Modal.Header closeButton>
          <Modal.Title>Confirmer la mise en vente</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Voulez-vous vraiment mettre cette carte en vente ?</p>
          {selectedCard && (
            <div style={{ textAlign: 'center' }}>
              <img src={selectedCard.img} alt={`Carte ${selectedCard.num}`} style={{ width: '80px', borderRadius: '8px', marginBottom: '10px' }} />
              <p><strong>Numéro :</strong> {selectedCard.num}</p>
              <p><strong>Collection :</strong> {selectedCard.nameCollection}</p>
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Annuler
          </Button>
          <Button variant="primary" onClick={handleListCardForSale}>
            Confirmer la mise en vente
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default UserCard;
