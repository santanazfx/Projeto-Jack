import { BagIcon } from './Icons';

function JackLogo({ onHome }) {
  return <button className="jack-logo" onClick={onHome} aria-label="JACK BURGER, ir ao início">
    <span className="jack-logo__badge jack-logo__badge--brand">
      <img src="/images/brand/jack-burger-logo.jpeg" alt="Logo Jack Burger" />
    </span>
    <span className="jack-logo__copy">
      <strong>JACK</strong>
      <small>BURGER</small>
    </span>
  </button>;
}

export function Header({ cartCount, onCart, onHome }) {
  return <header className="app-header">
    <JackLogo onHome={onHome} />
    <button className="header-cart" onClick={onCart} aria-label={`Abrir sacola com ${cartCount} item${cartCount === 1 ? '' : 's'}`}>
      <BagIcon />
      <span>{cartCount}</span>
    </button>
  </header>;
}
