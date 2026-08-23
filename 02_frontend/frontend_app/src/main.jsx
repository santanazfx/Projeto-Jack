import { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { BottomNavigation } from './components/BottomNavigation';
import { CatalogPage } from './components/CatalogPage';
import { FeaturedProducts } from './components/FeaturedProducts';
import { CartPage, ConfirmationPage, EmptyCartPage, OrderStatusPage, ProductCustomizerPage, ServiceModeIntro } from './components/FlowPages';
import { CashPage } from './components/CashPage';
import { Header } from './components/Header';
import { Hero } from './components/Hero';
import { QuickCategories } from './components/QuickCategories';
import './styles.css';

const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const deliveryFee = 6;

async function api(path, options) {
  const response = await fetch(path, options);
  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.message || 'Não foi possível concluir sua solicitação.');
  }
  return response.status === 204 ? null : response.json();
}

function CashLogin({ onLoggedIn, onBack }) {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  async function submit(event) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    setSubmitting(true);
    setError('');
    try {
      const user = await api('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: form.get('username'), password: form.get('password') })
      });
      onLoggedIn(user);
    } catch (loginError) {
      setError(loginError.message);
    } finally {
      setSubmitting(false);
    }
  }

  return <main className="cash-login-page">
    <section className="cash-login-card">
      <img src="/images/brand/jack-burger-logo.jpeg" alt="Jack Burguer" />
      <p>MÓDULO RESTRITO</p>
      <h1>Acesso ao caixa</h1>
      <span>Entre com as credenciais da equipe para operar comandas e pagamentos.</span>
      <form onSubmit={submit}>
        <label>Usuário<input name="username" required autoComplete="username" placeholder="Usuário do caixa" /></label>
        <label>Senha<input name="password" type="password" required autoComplete="current-password" placeholder="Sua senha" /></label>
        {error && <div className="cash-login-error">{error}</div>}
        <button className="cash-primary" disabled={submitting}>{submitting ? 'Entrando...' : 'Entrar no caixa'}</button>
      </form>
      <button className="cash-login-back" onClick={onBack}>← Voltar ao site</button>
    </section>
  </main>;
}

function CashAccessDenied({ onLogout, onBack }) {
  return <main className="cash-login-page">
    <section className="cash-login-card">
      <p>MÓDULO RESTRITO</p>
      <h1>Acesso não autorizado</h1>
      <span>Este usuário não possui a permissão necessária para operar o caixa.</span>
      <button className="cash-primary" onClick={onLogout}>Sair desta conta</button>
      <button className="cash-login-back" onClick={onBack}>← Voltar ao site</button>
    </section>
  </main>;
}

function Modal({ children, onClose, className = '' }) {
  return <div className="modal-backdrop" role="presentation" onMouseDown={onClose}>
    <section className={`modal-panel ${className}`} role="dialog" aria-modal="true" onMouseDown={event => event.stopPropagation()}>
      <button className="close-button" onClick={onClose} aria-label="Fechar">×</button>
      {children}
    </section>
  </div>;
}

function CartDrawer({ cart, service, onClose, onChangeQuantity, onCheckout }) {
  const subtotal = cart.reduce((sum, item) => sum + item.product.price * item.quantity, 0);
  const fee = service === 'DELIVERY' && cart.length ? deliveryFee : 0;

  return <>
    <div className="drawer-backdrop" onClick={onClose} />
    <aside className="cart-drawer" aria-label="Sacola Jack">
      <header className="drawer-header">
        <div><p className="drawer-eyebrow">SEU PEDIDO</p><h2>Sacola Jack</h2></div>
        <button className="close-button" onClick={onClose} aria-label="Fechar">×</button>
      </header>
      {cart.length === 0 ? <div className="cart-empty">
        <span aria-hidden="true">🍔</span>
        <strong>Sua sacola está vazia</strong>
        <p>Escolha algo delicioso no nosso cardápio.</p>
      </div> : <>
        <div className="cart-items">{cart.map(({ product, quantity }) => <article className="cart-item" key={product.id}>
          <img src={product.image} alt="" />
          <div>
            <h3>{product.name}</h3>
            <p>{money.format(product.price)} cada</p>
            <div className="quantity">
              <button onClick={() => onChangeQuantity(product.id, -1)} aria-label={`Remover uma unidade de ${product.name}`}>−</button>
              <strong>{quantity}</strong>
              <button onClick={() => onChangeQuantity(product.id, 1)} aria-label={`Adicionar uma unidade de ${product.name}`}>+</button>
            </div>
          </div>
          <strong>{money.format(product.price * quantity)}</strong>
        </article>)}</div>
        <footer className="cart-footer">
          <div className="price-row"><span>Subtotal</span><strong>{money.format(subtotal)}</strong></div>
          <div className="price-row"><span>Taxa de entrega</span><strong>{fee ? money.format(fee) : 'Grátis'}</strong></div>
          <div className="price-row total"><span>Total</span><strong>{money.format(subtotal + fee)}</strong></div>
          <button className="checkout-button" onClick={onCheckout}>Continuar pedido <span>→</span></button>
        </footer>
      </>}
    </aside>
  </>;
}

function Checkout({ service, onServiceChange, cart, onClose, onConfirmed }) {
  const [submitting, setSubmitting] = useState(false);
  const isTable = service === 'TABLE';

  async function submit(event) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    setSubmitting(true);
    try {
      const order = await api('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          customerName: form.get('customerName'),
          phone: form.get('phone'),
          serviceType: service,
          tableNumber: form.get('tableNumber') || null,
          address: service === 'DELIVERY' ? {
            zipCode: form.get('cep'),
            street: form.get('street'),
            number: form.get('number'),
            complement: form.get('complement') || null,
            neighborhood: form.get('neighborhood'),
            reference: form.get('reference') || null
          } : null,
          paymentMethod: form.get('paymentMethod'),
          pickupTime: form.get('pickupTime') || null,
          notes: form.get('notes') || null,
          items: cart.map(({ product, quantity, extras, removals, notes }) => ({ productId: product.id, quantity, extras, removals, notes }))
        })
      });
      onConfirmed(order);
    } catch (error) {
      window.alert(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  return <Modal onClose={onClose} className="checkout-modal">
    <p className="drawer-eyebrow">QUASE LÁ</p>
    <h2>Finalizar pedido</h2>
    <form className="checkout-form" onSubmit={submit}>
      <fieldset>
        <legend>Como você quer receber?</legend>
        <div className="payment-options service-options">
          <label><input type="radio" checked={service === 'DELIVERY'} onChange={() => onServiceChange('DELIVERY')} /> Delivery</label>
          <label><input type="radio" checked={service === 'PICKUP'} onChange={() => onServiceChange('PICKUP')} /> Retirada</label>
          <label><input type="radio" checked={service === 'TABLE'} onChange={() => onServiceChange('TABLE')} /> Estou na mesa</label>
        </div>
      </fieldset>
      <div className="form-grid">
        <label>Nome completo<input name="customerName" required placeholder="Como podemos te chamar?" /></label>
        <label>WhatsApp<input name="phone" required placeholder="(00) 00000-0000" /></label>
      </div>
      {isTable
        ? <label>Número da mesa<input name="tableNumber" required placeholder="Ex.: Mesa 08" /></label>
        : service === 'DELIVERY' ? <div className="checkout-address"><div className="form-grid"><label>CEP<input name="cep" required placeholder="00000-000" /></label><label>Número<input name="number" required placeholder="123" /></label></div><label>Rua<input name="street" required placeholder="Rua e avenida" /></label><div className="form-grid"><label>Complemento<input name="complement" placeholder="Apto, bloco..." /></label><label>Bairro<input name="neighborhood" required placeholder="Seu bairro" /></label></div><label>Referência<input name="reference" placeholder="Ponto de referência" /></label></div> : <label>Horário de retirada<input name="pickupTime" placeholder="Assim que estiver pronto" /></label>}
      <fieldset>
        <legend>Pagamento</legend>
        <div className="payment-options">
          <label><input type="radio" name="paymentMethod" value="Dinheiro" defaultChecked /> Dinheiro</label>
          <label><input type="radio" name="paymentMethod" value="Cartão" /> Cartão</label>
          <label><input type="radio" name="paymentMethod" value="PIX" /> PIX</label>
        </div>
      </fieldset>
      <label>Observações<textarea name="notes" placeholder="Ex.: sem cebola, ponto da carne..." /></label>
      <button className="checkout-button" disabled={submitting}>{submitting ? 'Confirmando...' : <>Confirmar pedido <span>→</span></>}</button>
      <p className="payment-hint">O pagamento é realizado no recebimento do pedido.</p>
    </form>
  </Modal>;
}

function Tracking({ onClose }) {
  const [result, setResult] = useState(null);
  const [message, setMessage] = useState('');

  async function submit(event) {
    event.preventDefault();
    const code = new FormData(event.currentTarget).get('trackingCode').trim().toUpperCase();
    try {
      setResult(await api(`/api/orders/${encodeURIComponent(code)}`));
      setMessage('');
    } catch (error) {
      setResult(null);
      setMessage(error.message);
    }
  }

  return <Modal onClose={onClose} className="tracking-modal">
    <p className="drawer-eyebrow">ACOMPANHAMENTO</p>
    <h2>Cadê meu pedido?</h2>
    <form className="tracking-form" onSubmit={submit}>
      <label>Código do pedido<input name="trackingCode" required placeholder="Ex.: JACK-ABC123" /></label>
      <button className="checkout-button">Consultar</button>
    </form>
    {(result || message) && <div className="tracking-result">{result
      ? <><p>Pedido <b>{result.trackingCode}</b></p><strong>{result.status}</strong><p>Previsão: {result.estimatedTime}</p><p>Total: {money.format(result.total)}</p></>
      : <p>{message}</p>}
    </div>}
  </Modal>;
}

function Profile({ onClose }) {
  const [saved, setSaved] = useState(false);

  function save(event) {
    event.preventDefault();
    setSaved(true);
  }

  return <Modal onClose={onClose} className="profile-modal">
    <p className="drawer-eyebrow">MINHA CONTA</p>
    <h2>Seu perfil</h2>
    <form className="checkout-form" onSubmit={save}>
      <label>Nome<input name="profileName" placeholder="Seu nome" /></label>
      <label>WhatsApp<input name="profilePhone" placeholder="(00) 00000-0000" /></label>
      <button className="checkout-button">Salvar dados <span>→</span></button>
      {saved && <p className="payment-hint">Dados atualizados nesta sessão.</p>}
    </form>
  </Modal>;
}

function ProductPreview({ product, onClose, onAdd, added }) {
  const canOrder = product.price != null;

  function addProduct() {
    if (!canOrder) return;
    onAdd(product);
    onClose();
  }

  return <Modal onClose={onClose} className="product-preview-modal">
    <img className="product-preview-modal__image" src={product.image} alt={product.name} />
    <p className="drawer-eyebrow">{product.bread}</p>
    <h2>{product.name}</h2>
    <p className="product-preview-modal__copy">{product.description}</p>
    <strong className="product-preview-modal__price">{canOrder ? money.format(product.price) : 'PREÇO A DEFINIR'}</strong>
    <button className="checkout-button" disabled={!canOrder} onClick={addProduct}>{added ? 'Adicionar mais uma' : 'Adicionar à sacola'} <span>→</span></button>
  </Modal>;
}

function App() {
  const initialView = new URLSearchParams(window.location.search).get('view') === 'cash' ? 'cash' : 'home';
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState([]);
  const [service, setService] = useState('DELIVERY');
  const [screen, setScreen] = useState(null);
  const [lastOrder, setLastOrder] = useState(null);
  const [lastOrderItems, setLastOrderItems] = useState([]);
  const [activeCategory, setActiveCategory] = useState('Pão de hambúrguer');
  const [activeTab, setActiveTab] = useState(initialView === 'cash' ? 'cash' : 'home');
  const [view, setView] = useState(initialView);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [cashUser, setCashUser] = useState(null);
  const [checkingCashAccess, setCheckingCashAccess] = useState(initialView === 'cash');

  useEffect(() => {
    api('/api/products').then(setProducts).catch(() => {
      // Os cards de referência continuam visíveis mesmo se a API estiver reiniciando.
    });
  }, []);

  useEffect(() => {
    if (view !== 'cash') return;
    setCheckingCashAccess(true);
    api('/api/auth/me')
      .then(setCashUser)
      .catch(() => setCashUser(false))
      .finally(() => setCheckingCashAccess(false));
  }, [view]);

  const cartCount = cart.reduce((sum, item) => sum + item.quantity, 0);
  const addToCart = (product, configuration = {}) => {
    if (product.price == null) return;
    const extras = configuration.extras || [];
    const removals = configuration.removals || [];
    const notes = configuration.notes?.trim() || '';
    const quantity = configuration.quantity || 1;
    const extrasTotal = extras.reduce((sum, extra) => sum + ({ Bacon: 4, 'Cheddar Extra': 3, 'Cebola Caramelizada': 3.5, 'Molho Especial': 2 }[extra] || 0), 0);
    const unitPrice = product.price + extrasTotal;
    const key = `${product.id}:${extras.join('|')}:${removals.join('|')}:${notes}`;
    setCart(items => {
      const current = items.find(item => item.key === key);
      return current
        ? items.map(item => item.key === key ? { ...item, quantity: item.quantity + quantity } : item)
        : [...items, { key, product, quantity, unitPrice, extras, removals, notes }];
    });
  };
  const changeQuantity = (key, amount) => setCart(items => items
    .map(item => item.key === key ? { ...item, quantity: item.quantity + amount } : item)
    .filter(item => item.quantity > 0));
  const goHome = () => {
    window.history.replaceState({}, '', window.location.pathname);
    setView('home');
    setActiveCategory('Pão de hambúrguer');
    setActiveTab('home');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };
  const openCatalog = (bread = 'Todos') => {
    window.history.replaceState({}, '', window.location.pathname);
    setView('catalog');
    setActiveTab('menu');
    setActiveCategory(bread);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };
  const openCart = () => {
    setView(cart.length ? 'cart' : 'empty-cart');
    setActiveTab('orders');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };
  const openProduct = product => {
    setSelectedProduct(product);
    setView('product');
    setActiveTab('menu');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };
  const selectCategory = category => openCatalog(category);
  const navigate = tab => {
    if (tab === 'home') goHome();
    if (tab === 'menu') openCatalog();
    if (tab === 'orders') { setActiveTab(tab); lastOrder ? setView('status') : setScreen('tracking'); }
    if (tab === 'profile') { setActiveTab(tab); setScreen('profile'); }
  };
  const logoutCash = async () => {
    try {
      await api('/api/auth/logout', { method: 'POST' });
    } finally {
      setCashUser(false);
    }
  };
  const canUseCash = cashUser && cashUser.roles?.some(role => ['CAIXA', 'GERENTE', 'ADMIN'].includes(role));
  const completeOrder = order => {
    setLastOrder(order);
    setLastOrderItems(cart);
    setCart([]);
    setScreen(null);
    setView('confirmation');
    setActiveTab('orders');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return <div className={`app-frame ${view === 'cash' ? 'app-frame--cash' : ''}`}>
    {['home', 'delivery', 'table'].includes(view) && <Header cartCount={cartCount} onCart={openCart} onHome={goHome} />}
    {view === 'home' && <main>
      <Hero onOrder={() => openCatalog()} />
      <QuickCategories activeCategory={activeCategory} onSelect={selectCategory} onViewAll={() => openCatalog()} />
      <FeaturedProducts products={products} cart={cart} onAdd={addToCart} onOpen={openProduct} />
    </main>}
    {view === 'delivery' && <main>
      <ServiceModeIntro mode="DELIVERY" onStartOrder={() => { setService('DELIVERY'); openCatalog(); }} onTrackOrder={() => { setService('PICKUP'); openCatalog(); }} />
      <Hero onOrder={() => openCatalog()} />
      <QuickCategories activeCategory={activeCategory} onSelect={selectCategory} onViewAll={() => openCatalog()} />
      <FeaturedProducts products={products} cart={cart} onAdd={addToCart} onOpen={openProduct} />
    </main>}
    {view === 'table' && <main>
      <ServiceModeIntro mode="TABLE" onStartOrder={() => { setService('TABLE'); openCatalog(); }} onTrackOrder={() => lastOrder && setView('status')} onWaiter={() => window.alert('Chamado enviado para a equipe.')} onAccount={() => openCart()} />
      <QuickCategories activeCategory={activeCategory} onSelect={selectCategory} onViewAll={() => openCatalog()} />
      <FeaturedProducts products={products} cart={cart} onAdd={addToCart} onOpen={openProduct} />
    </main>}
    {view === 'catalog' && <CatalogPage
      products={products}
      selectedBread={activeCategory}
      onBreadChange={setActiveCategory}
      cart={cart}
      onAdd={addToCart}
      onBack={goHome}
      onOpen={openProduct}
    />}
    {view === 'product' && selectedProduct && <ProductCustomizerPage product={selectedProduct} onBack={() => openCatalog()} onAdd={configuration => { addToCart(selectedProduct, configuration); setView('cart'); setActiveTab('orders'); }} />}
    {view === 'cart' && (cart.length ? <CartPage cart={cart} service={service} onBack={goHome} onCatalog={() => openCatalog()} onChangeQuantity={changeQuantity} onCheckout={() => setScreen('checkout')} /> : <EmptyCartPage onBack={goHome} onCatalog={() => openCatalog()} />)}
    {view === 'empty-cart' && <EmptyCartPage onBack={goHome} onCatalog={() => openCatalog()} />}
    {view === 'confirmation' && lastOrder && <ConfirmationPage order={lastOrder} cart={lastOrderItems} onTrack={() => setView('status')} onHome={goHome} />}
    {view === 'status' && lastOrder && <OrderStatusPage order={lastOrder} onBack={goHome} onWaiter={() => window.alert('Chamado enviado para a equipe.')} />}
    {view === 'cash' && (checkingCashAccess
      ? <main className="cash-login-page"><p className="cash-login-loading">Verificando acesso ao caixa...</p></main>
      : canUseCash
        ? <CashPage api={api} onBack={goHome} onLogout={logoutCash} user={cashUser} />
        : cashUser
          ? <CashAccessDenied onLogout={logoutCash} onBack={goHome} />
          : <CashLogin onLoggedIn={setCashUser} onBack={goHome} />)}
    <BottomNavigation activeTab={activeTab} onNavigate={navigate} />

    {screen === 'checkout' && <Checkout service={service} onServiceChange={setService} cart={cart} onClose={() => setScreen(null)} onConfirmed={completeOrder} />}
    {screen === 'tracking' && <Tracking onClose={() => setScreen(null)} />}
    {screen === 'profile' && <Profile onClose={() => setScreen(null)} />}
  </div>;
}

createRoot(document.getElementById('root')).render(<App />);
