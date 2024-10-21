import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from 'react-bootstrap/Card';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Web3 from 'web3';

const Users: React.FC = () => {
  const [accounts, setAccounts] = useState<string[]>([]);

  const init = async () => {
    if (window.ethereum) {
      try {
        // Demander la connexion à MetaMask
        const web3 = new Web3(window.ethereum);
        const allAccounts = await web3.eth.requestAccounts();
        setAccounts(allAccounts);
      } catch (error) {
        console.error('Erreur lors de la récupération des comptes:', error);
      }
    } else {
      console.error('MetaMask non détecté');
    }
  };

  useEffect(() => {
    init();
  }, []);

  return (
    <Container>
      <h1>Liste des comptes Ethereum</h1>
      <Row>
        {accounts.length > 0 ? (
          accounts.map((account, index) => (
            <Col key={index} md={4} lg={3} style={{ marginBottom: '20px' }}>
              <Card
                style={{
                  position: 'relative',
                  cursor: 'pointer',
                  height: '200px',
                  textAlign: 'center',
                  overflow: 'hidden',
                  border: '2px solid white',
                  borderRadius: '10px',
                  backgroundColor: 'rgba(0, 0, 0, 0.5)',
                }}
              >
                {/* Contenu de la carte */}
                <div style={{ position: 'relative', zIndex: 2, color: 'white' }}>
                  <Card.Body>
                    <Card.Title>Compte {index + 1}</Card.Title>
                    <Card.Text>{account}</Card.Text>
                    <Link to={`/utilisateur/${account}`} className="btn btn-light">
                      Voir les détails
                    </Link>
                  </Card.Body>
                </div>
              </Card>
            </Col>
          ))
        ) : (
          <Col>
            <p>Aucun compte trouvé</p>
          </Col>
        )}
      </Row>
    </Container>
  );
};

export default Users;
