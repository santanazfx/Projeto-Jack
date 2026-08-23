import { useMemo, useState } from 'react';
import { ArrowLeftIcon, ArrowRightIcon } from './Icons';

const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

export const extraOptions = [
  { name: 'Bacon', price: 4 },
  { name: 'Cheddar Extra', price: 3 },
  { name: 'Cebola Caramelizada', price: 3.5 },
  { name: 'Molho Especial', price: 2 }
];

function PageHeader({ title, onBack }) {
  return <header className="figma-page-header">
    <button type="button" onClick={onBack} aria-label="Voltar"><ArrowLeftIcon /></button>
    <h1>{title}</h1>
  </header>;
}

export function ServiceModeIntro({ mode, onStartOrder, onTrackOrder, onWaiter, onAccount }) {
  if (mode === 'TABLE') {
    return <section className="service-mode-intro service-mode-intro--table">
      <span className="service-mode-intro__pill">MESA 08</span>
      <p>Faça seu pedido direto pelo celular. Cozinha notificada imediatamente.</p>
      <button className="service-mode-intro__primary" type="button" onClick={onStartOrder}>FAZER PEDIDO <ArrowRightIcon /></button>
      <div className="service-mode-intro__actions">
        <button type="button" onClick={onWaiter}>🙋 Chamar Garçom</button>
        <button type="button" onClick={onAccount}>🧾 Ver Conta</button>
      </div>
    </section>;
  }

  return <section className="service-mode-intro service-mode-intro--delivery">
    <div><span aria-hidden="true">🚀</span><div><strong>DELIVERY JACK</strong><p>Receba seu pedido onde estiver com segurança e agilidade.</p></div></div>
    <div className="service-mode-intro__delivery-actions">
      <button type="button" className="is-selected" onClick={onStartOrder}>🏠 Entregar em Casa</button>
      <button type="button" onClick={onTrackOrder}>🏪 Retirar na Loja</button>
    </div>
  </section>;
}

export function ProductCustomizerPage({ product, onBack, onAdd }) {
  const [quantity, setQuantity] = useState(1);
  const [extras, setExtras] = useState([]);
  const [removals, setRemovals] = useState([]);
  const [notes, setNotes] = useState('');
  const removable = useMemo(() => ['Alface', 'Tomate', 'Cebola'].filter(item => product.description.toLocaleLowerCase('pt-BR').includes(item.toLocaleLowerCase('pt-BR'))), [product]);
  const extrasTotal = extras.reduce((sum, name) => sum + (extraOptions.find(option => option.name === name)?.price || 0), 0);
  const total = (product.price + extrasTotal) * quantity;
  const toggle = (value, list, setList) => setList(current => current.includes(value) ? current.filter(item => item !== value) : [...current, value]);

  return <section className="customizer-page">
    <PageHeader title="" onBack={onBack} />
    <main className="customizer-page__content">
      <img src={product.image} alt={product.name} className="customizer-page__image" />
      <h2>{product.name}</h2>
      <p className="customizer-page__description">{product.description}</p>
      <strong className="customizer-page__price">{money.format(product.price)}</strong>

      <section className="customizer-section customizer-section--quantity">
        <h3>Quantidade</h3>
        <div className="customizer-quantity"><button type="button" onClick={() => setQuantity(value => Math.max(1, value - 1))}>−</button><strong>{quantity}</strong><button type="button" onClick={() => setQuantity(value => value + 1)}>+</button></div>
      </section>
      <section className="customizer-section">
        <h3>PERSONALIZE SEU PEDIDO</h3>
        <p className="customizer-section__label">Adicionais</p>
        {extraOptions.map(option => <label className="customizer-option" key={option.name}><span><input type="checkbox" checked={extras.includes(option.name)} onChange={() => toggle(option.name, extras, setExtras)} />{option.name}</span><b>+ {money.format(option.price)}</b></label>)}
      </section>
      {removable.length > 0 && <section className="customizer-section">
        <p className="customizer-section__label">Remover ingredientes</p>
        {removable.map(item => <label className="customizer-option" key={item}><span><input type="checkbox" checked={removals.includes(item)} onChange={() => toggle(item, removals, setRemovals)} />Remover {item}</span></label>)}
      </section>}
      <section className="customizer-section">
        <p className="customizer-section__label">Observações</p>
        <textarea value={notes} onChange={event => setNotes(event.target.value)} placeholder="Alguma observação para a cozinha?" />
      </section>
    </main>
    <footer className="customizer-page__footer"><button type="button" onClick={() => onAdd({ quantity, extras, removals, notes })}>ADICIONAR AO PEDIDO — {money.format(total)}</button></footer>
  </section>;
}

export function EmptyCartPage({ onBack, onCatalog }) {
  return <section className="empty-cart-page"><PageHeader title="MEU PEDIDO" onBack={onBack} /><main><span>🍔</span><h2>Seu pedido está vazio.</h2><p>Que tal escolher um burger?</p><button type="button" onClick={onCatalog}>VER CARDÁPIO <ArrowRightIcon /></button></main></section>;
}

export function CartPage({ cart, service, onBack, onCatalog, onChangeQuantity, onCheckout }) {
  const subtotal = cart.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);
  const fee = service === 'DELIVERY' && cart.length ? 6 : 0;

  return <section className="figma-cart-page"><PageHeader title="MEU PEDIDO" onBack={onBack} />
    <p className="figma-cart-page__notice">Adicionado ao pedido ✓</p>
    <main className="figma-cart-page__items">{cart.map(item => <article className="figma-cart-item" key={item.key}>
      <img src={item.product.image} alt={item.product.name} /><div><h2>{item.product.name}</h2>{item.extras.length > 0 && <p>+ {item.extras.join(', ')}</p>}{item.removals.length > 0 && <p>Sem {item.removals.join(', ')}</p>}{item.notes && <p>{item.notes}</p>}<div className="figma-cart-item__quantity"><button type="button" onClick={() => onChangeQuantity(item.key, -1)}>−</button><strong>{item.quantity}</strong><button type="button" onClick={() => onChangeQuantity(item.key, 1)}>+</button></div></div><strong>{money.format(item.unitPrice * item.quantity)}</strong>
    </article>)}</main>
    <footer className="figma-cart-page__summary"><div><span>Subtotal</span><strong>{money.format(subtotal)}</strong></div><div><span>Taxa de entrega</span><strong>{fee ? money.format(fee) : 'Grátis'}</strong></div><div className="is-total"><span>TOTAL</span><strong>{money.format(subtotal + fee)}</strong></div><button type="button" onClick={onCheckout}>CONTINUAR PEDIDO <ArrowRightIcon /></button><button type="button" className="figma-cart-page__continue" onClick={onCatalog}>Continuar comprando</button></footer>
  </section>;
}

export function ConfirmationPage({ order, cart, onTrack, onHome }) {
  return <section className="confirmation-page"><main><span className="confirmation-page__icon">✓</span><h1>PEDIDO CONFIRMADO! 🍔</h1><p className="confirmation-page__code">Pedido #{order.trackingCode.replace('JACK-', '')}</p><p>Seu pedido já foi enviado para a cozinha.</p><div className="confirmation-page__time">⏱ Tempo estimado: ~{order.estimatedTime}</div></main><section className="confirmation-page__items"><h2>ITENS DO PEDIDO</h2>{cart.map(item => <div key={item.key}><span>{item.quantity}x {item.product.name}</span><strong>{money.format(item.unitPrice * item.quantity)}</strong></div>)}<div><span>Taxa de entrega</span><strong>{order.serviceType === 'Delivery' ? money.format(6) : 'Grátis'}</strong></div><div className="is-total"><span>TOTAL</span><strong>{money.format(order.total)}</strong></div></section><footer><button type="button" onClick={onTrack}>ACOMPANHAR PEDIDO</button><button type="button" onClick={onHome}>VOLTAR AO INÍCIO</button></footer></section>;
}

export function OrderStatusPage({ order, onBack, onWaiter }) {
  const isTable = order.serviceType === 'Mesa';
  const isPickup = order.serviceType === 'Retirada';
  const stages = isTable ? [['Pedido recebido', 'Sua solicitação foi para a cozinha'], ['Em preparação', 'Preparando seus pratos'], ['Pedido pronto', 'Pronto para ser servido'], ['Entregue à mesa', 'Aproveite sua refeição!']] : isPickup ? [['Pedido recebido', 'Sua solicitação foi registrada'], ['Em preparação', 'O chef está montando seu burger'], ['Pronto para retirada', 'Seu pedido está pronto na loja'], ['Retirado', 'Bom apetite!']] : [['Pedido recebido', 'Sua solicitação foi registrada'], ['Em preparação', 'O chef está montando seu burger'], ['Preparando seu pedido', 'Quase pronto para a saída'], ['Saiu para entrega', 'O entregador está à caminho'], ['Entregue', 'Bom apetite!']];
  const active = Math.max(0, stages.findIndex(([title]) => title.toLocaleLowerCase('pt-BR').includes(order.status.toLocaleLowerCase('pt-BR').replace('pedido ', ''))));
  return <section className="order-status-page"><PageHeader title="MEU PEDIDO" onBack={onBack} /><main><p className="order-status-page__code">{isTable ? `Mesa ${order.tableNumber || '08'} • ` : ''}Pedido #{order.trackingCode.replace('JACK-', '')}</p>{stages.map(([title, copy], index) => <article className={index <= active ? 'is-active' : ''} key={title}><span>{index < active ? '✓' : index === active ? '●' : '○'}</span><div><h2>{title}</h2><p>{copy}</p></div></article>)}</main><footer>{isTable ? <><h2>MESA DO PEDIDO</h2><strong>Mesa {order.tableNumber || '08'}</strong><p>Acompanhe o painel para atualizações</p><button type="button" onClick={onWaiter}>CHAMAR GARÇOM</button></> : isPickup ? <><h2>RETIRADA NA LOJA</h2><strong>Seu pedido será separado para retirada.</strong></> : <><h2>ENDEREÇO DE ENTREGA</h2><strong>{order.address || 'Endereço informado no checkout'}</strong></>}</footer></section>;
}
