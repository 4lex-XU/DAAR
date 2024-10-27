import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { WalletProps, Card } from '../apiPokeTCG/types';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import MainABI from '@/abis/Main.json';
import Web3 from 'web3';

const UserCard: React.FC<WalletProps> = ({ wallet }) => {
  const { account } = useParams();
  const [cards, setCardsValue] = useState<Card[]>([]);
  const [selectedCard, setSelectedCard] = useState<Card>();

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

  const handleListCardForSale = async () => {
    if (!selectedCard) return;

    try {
        await loadWeb3();
        window.contract = await loadContract();

        await window.contract.methods
            .listCardForSale(selectedCard?.nameCollection, selectedCard?.num)
            .send({ from: account });
            
        alert('Carte mise en vente avec succès !');
        setSelectedCard(undefined);
    } catch (error) {
        console.error("Erreur lors de la mise en vente de la carte :", error);
    }
  };

  useEffect(() => {
    getCardsByOwner();
  },[])

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
                <div className="card-item">
                  <img src={card.img} alt={`Carte ${card.num}`} style={{ width: '100%' }} />
                  <button onClick={() => setSelectedCard(card)}>
                    Mettre en vente
                  </button>
                </div>
              </Col>
            ))}
          </Row>
        </Container>
      ) : (
        <p>Aucune carte trouvée</p>
      )}

    {selectedCard && (
        <div>
            <h3>Mettre la carte en vente</h3>
            <button onClick={handleListCardForSale}>Confirmer la mise en vente</button>
        </div>
    )}
    </div>
  );
};

export default UserCard;
