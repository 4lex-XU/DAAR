import axios from 'axios';
import { API_URL, API_KEY } from './apiConfig';
import { PokemonSet, PokemonCard } from './types';

// Fonction pour récupérer tous les sets de cartes Pokémon
export async function getPokemonSets(): Promise<PokemonSet[]> {
  try {
    const response = await axios.get<{ data: PokemonSet[] }>(`${API_URL}sets`, {
      headers: {
        'X-Api-Key': API_KEY,
      },
    });
    return response.data.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des sets:', error);
    throw error;
  }
}

// Fonction pour récupérer les cartes d'un set spécifique
export async function getCardsFromSet(setId: string): Promise<PokemonCard[]> {
  try {
    const response = await axios.get<{ data: PokemonCard[] }>(
      `${API_URL}cards?q=set.id:${setId}`,
      {
        headers: {
          'X-Api-Key': API_KEY,
        },
      }
    );
    return response.data.data;
  } catch (error) {
    console.error(`Erreur lors de la récupération des cartes du set ${setId}:`, error);
    throw error;
  }
}
