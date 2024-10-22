import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { WalletProps, Card } from '../apiPokeTCG/types';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

const UserCard: React.FC<WalletProps> = ({ wallet }) => {
  const [cards, setCardsValue] = useState<Card[]>([]);
  const { account } = useParams();

  useEffect(() => {
    if (wallet?.contract && wallet?.details.account) {
      getCardsByOwner();
    }
  }, [wallet]);

  async function getCardsByOwner() {
    if (wallet?.contract && wallet?.details.account) {
      try {
        const data = await wallet.contract.getCardsByOwner(account!);
        const formattedCards = data.map((tuple: [string, string]) => ({
          num: tuple[0],
          img: tuple[1]
        }));
        setCardsValue(formattedCards);
        console.log('getCardsByOwner: ' + wallet.details.account + ' réussie !');
      } catch (err) {
        console.log('Erreur lors du getCardsByOwner.');
        console.error(err);
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
                </div>
              </Col>
            ))}
          </Row>
        </Container>
      ) : (
        <p>Aucune carte trouvée</p>
      )}
    </div>
  );
};

export default UserCard;
