import * as ethereum from '@/lib/ethereum'
import * as main from '@/lib/main'

export interface PokemonSet {
  id: string;
  name: string;
  series: string;
  printedTotal: number;
  total: number;
  releaseDate: string;
  images: {
    symbol: string;
    logo: string;
  };
}

export interface PokemonCard {
  id: string;
  name: string;
  rarity: string;
  images: {
    small: string;
    large: string;
    };
}

export interface WalletProps {
  wallet: {
      details: ethereum.Details;
      contract: main.Main;
  };
}

export interface Card {
  num: string;
  img: string;
  nameCollection: string;
  cardCountCollection: number;
}

export interface Sale {
  saleId: number;
  card: Card;
}