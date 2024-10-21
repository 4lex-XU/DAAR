import React from 'react';
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom';
import { WalletProps, Card } from '../apiPokeTCG/types';

const UserCard: React.FC<WalletProps> = ({wallet}) => {
    const [cards, setCardsValue] = useState<Card[]>([])
    const { account } = useParams();

    useEffect(() => {
      if (wallet?.contract && wallet?.details.account) {
        getCardsByOwner();
      }
    },[wallet])
  
    async function getCardsByOwner() {
        if (wallet?.contract && wallet?.details.account) {
            try {
            const data = await wallet.contract.getCardsByOwner(account!)
            const formattedCards = data.map((tuple: [string, string]) => ({
                num: tuple[0],
                img: tuple[1]
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
        <h1>Mes Cartes</h1>
        <ul>
            {cards.length > 0 ? (
            cards.map((card, index) => (
                <li key={index}>
                Carte #{card.num}: {card.img}
                </li>
            ))
            ) : (
                <li>Aucune carte trouvée</li>
            )}
        </ul>
        </div>
    );
}

export default UserCard;