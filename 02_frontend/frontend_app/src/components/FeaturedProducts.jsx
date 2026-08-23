const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

export function productPriceLabel(product) {
  return product.price == null ? 'PREÇO A DEFINIR' : money.format(product.price);
}

export function FeaturedProductCard({ product, onAdd, onOpen, added, height = 186 }) {
  const canOrder = product.price != null;

  return <article className="featured-product" style={{ '--product-height': `${height}px`, '--description-lines': 2 }}>
    <button className="featured-product__image" type="button" onClick={() => onOpen?.(product)} aria-label={`Ver ${product.name}, ${product.bread}`}>
      <img src={product.image} alt={product.name} />
    </button>
    <div className="featured-product__content">
      <button className="featured-product__open" type="button" onClick={() => onOpen?.(product)} aria-label={`Ver ${product.name}, ${product.bread}`}>
        <h3>{product.name}</h3>
        <p>{product.description || product.bread}</p>
      </button>
      <footer>
        <strong className={canOrder ? '' : 'is-pending'}>{productPriceLabel(product)}</strong>
        <button className={`featured-product__add ${added ? 'is-added' : ''}`} type="button" disabled={!canOrder} onClick={() => onAdd(product)}>
          {canOrder ? (added ? '✓ ADICIONADO' : '+ ADICIONAR') : 'AGUARDANDO PREÇO'}
        </button>
      </footer>
    </div>
  </article>;
}

export function FeaturedProducts({ products, cart, onAdd, onOpen }) {
  const displayedProducts = products.slice(0, 2);

  return <section id="favorites" className="featured-products" aria-labelledby="featured-products-title">
    <h2 id="featured-products-title">OS FAVORITOS DA JACK</h2>
    <div className="featured-products__list">
      {displayedProducts.map((product, index) => <FeaturedProductCard
        key={product.id}
        product={product}
        height={index === 0 ? 186 : 168}
        added={cart.some(item => item.product.id === product.id)}
        onAdd={onAdd}
        onOpen={onOpen}
      />)}
    </div>
  </section>;
}
