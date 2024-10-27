import React from 'react';
import { Container, Navbar, Nav } from 'react-bootstrap';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import PokemonSets from './PokemonSets';
import Users from './Users';
import UserCard from './UserCard';
import { WalletProps } from '../apiPokeTCG/types';
import Booster from './Booster';
import MarketPlace from './MarketPlace';

const Home: React.FC<WalletProps> = ({wallet}) => {
  return (
    <Router>
      {/* Barre de navigation */}
      <Navbar bg="dark" variant="dark" expand="lg">
        <Container>
          <Navbar.Brand as={Link} to="/">
            <img
               src="../../public/logo.png"
               alt="Logo Pokémon"
               width="100"
               height="auto"
             />
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              <Nav.Link as={Link} to="/">Home</Nav.Link>
              <Nav.Link as={Link} to="/sets">Sets</Nav.Link>
              <Nav.Link as={Link} to="/utilisateurs">Utilisateurs</Nav.Link>
              <Nav.Link as={Link} to="/boosters">Boosters</Nav.Link>
              <Nav.Link as={Link} to="/marketPlace">Market</Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      {/* Contenu des pages */}
      <Container className="mt-4">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/sets" element={<PokemonSets />} />
          <Route path="/utilisateurs" element={<Users />} />
          <Route path="/utilisateur/:account" element={<UserCard wallet={wallet} />} />
          <Route path="/boosters" element={<Booster wallet={wallet} />} />
          <Route path="/marketPlace" element={<MarketPlace />} />
        </Routes>
      </Container>
    </Router>
  );
};

// Composant pour la page d'accueil
const HomePage: React.FC = () => {
  return (
    <div>
      <h1>Bienvenue sur Mon TCG</h1>
      <p>Ceci est la page d'accueil. Explorez les sets ou les utilisateurs en utilisant la navigation ci-dessus.</p>
    </div>
  );
};

export default Home;