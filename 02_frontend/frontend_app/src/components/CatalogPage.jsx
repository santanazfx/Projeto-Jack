import { useMemo, useState } from 'react';
import { FeaturedProductCard } from './FeaturedProducts';
import { ArrowLeftIcon, SearchIcon } from './Icons';

const breadFilters = [
  { value: 'Todos', label: 'Todos' },
  { value: 'Pão de hambúrguer', label: 'Hambúrguer' },
  { value: 'Pão francês mata fome', label: 'Mata fome' },
  { value: 'Pão brioche', label: 'Brioche' }
];

export function CatalogPage({ products, selectedBread, onBreadChange, cart, onAdd, onBack, onOpen }) {
  const [query, setQuery] = useState('');
  const visibleProducts = useMemo(() => {
    const normalizedQuery = query.trim().toLocaleLowerCase('pt-BR');
    return products.filter(product => {
      const matchesBread = selectedBread === 'Todos' || product.bread === selectedBread;
      const searchable = `${product.name} ${product.description} ${product.bread}`.toLocaleLowerCase('pt-BR');
      return matchesBread && (!normalizedQuery || searchable.includes(normalizedQuery));
    });
  }, [products, query, selectedBread]);

  return <section className="catalog-page" aria-labelledby="catalog-title">
    <header className="catalog-page__header">
      <button onClick={onBack} aria-label="Voltar ao início"><ArrowLeftIcon /></button>
      <h1 id="catalog-title">BUSCA</h1>
    </header>
    <label className="catalog-search">
      <SearchIcon />
      <input value={query} onChange={event => setQuery(event.target.value)} placeholder="O que você está procurando?" type="search" />
    </label>
    <div className="catalog-filters" role="tablist" aria-label="Filtrar por tipo de pão">
      {breadFilters.map(filter => <button
        key={filter.value}
        role="tab"
        aria-selected={selectedBread === filter.value}
        className={selectedBread === filter.value ? 'is-selected' : ''}
        onClick={() => onBreadChange(filter.value)}
      >{filter.label}</button>)}
    </div>
    <section className="catalog-results" aria-labelledby="catalog-results-title">
      <h2 id="catalog-results-title">RESULTADOS</h2>
      {visibleProducts.length > 0
        ? <div className="catalog-results__list">{visibleProducts.map(product => <FeaturedProductCard
          key={product.id}
          product={product}
          height={166}
          added={cart.some(item => item.product.id === product.id)}
          onAdd={onAdd}
          onOpen={onOpen}
        />)}</div>
        : <div className="catalog-empty"><strong>Nenhum lanche encontrado.</strong><p>Tente outro nome ou tipo de pão.</p></div>}
    </section>
  </section>;
}
