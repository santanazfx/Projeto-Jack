import { ArrowRightIcon } from './Icons';

const comboJack = {
  id: 3,
  name: 'COMBO JACK',
  description: 'Burger + Batata + Refri',
  category: 'Combos',
  price: 39.9,
  image: '/images/jack-classic-figma.png'
};

export function PromotionBanner({ products, onAdd }) {
  const combo = products.find(product => product.id === comboJack.id) || comboJack;
  return <section className="promotion-section" aria-label="Promoção Combo Jack">
    <article className="promotion-banner">
      <div>
        <h2>COMBO JACK</h2>
        <p>Burger + Batata + Refri</p>
      </div>
      <div className="promotion-banner__price">
        <s>R$ 44,90</s>
        <strong>R$ 39,90</strong>
      </div>
      <button onClick={() => onAdd(combo)}>
        <span>QUERO ESSE</span>
        <ArrowRightIcon />
      </button>
    </article>
  </section>;
}
