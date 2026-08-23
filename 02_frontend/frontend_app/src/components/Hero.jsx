import { ArrowRightIcon } from './Icons';

export function Hero({ onOrder }) {
  return <section className="hero" aria-labelledby="hero-title">
    <div className="hero__copy">
      <span className="artisan-badge">100% ARTESANAL</span>
      <h1 id="hero-title">SEU BURGER.<br />DO SEU JEITO.</h1>
      <p>Escolha seu hambúrguer favorito, personalize seu pedido e aproveite.</p>
    </div>
    <div className="hero__cta-wrap">
      <button className="hero__cta" onClick={onOrder}>
        <span>PEDIR AGORA</span>
        <ArrowRightIcon />
      </button>
      <p>Pedido rápido • Fácil • Sem complicação</p>
    </div>
  </section>;
}
