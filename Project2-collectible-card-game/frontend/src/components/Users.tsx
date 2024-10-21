import React from 'react';
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom';
import Web3 from 'web3'

const Users: React.FC = () => {

  const [accounts, setAccounts] = useState<string[]>([])

  const init = async () => {
      if (window.ethereum) {
          try {
              // Demander la connexion à MetaMask
              const web3 = new Web3(window.ethereum)
              const allAccounts = await web3.eth.requestAccounts()
              setAccounts(allAccounts)
          } catch (error) {
              console.error('Erreur lors de la récupération des comptes:', error)
          }
      } else {
          console.error('MetaMask non détecté')
      }
  }

  useEffect(() => {
      init()
  }, [])

  return (
    <div>
      <h1>Liste des comptes Ethereum</h1>
      <ul>
      {accounts.length > 0 ? (
          accounts.map((account, index) => (
          <li key={index}>
            <Link to={`/utilisateur/${index}`}>{account}</Link>
          </li>
          ))
      ) : (
          <li>Aucun compte trouvé</li>
      )}
      </ul>
    </div>
  );
};

export default Users;
