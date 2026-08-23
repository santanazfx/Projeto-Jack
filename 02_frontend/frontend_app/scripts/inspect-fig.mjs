import { readFileSync, writeFileSync } from 'node:fs';
import { parseFig, nodeId } from 'openfig-core';

const [figFile, outputFile] = process.argv.slice(2);

if (!figFile || !outputFile) {
  throw new Error('Uso: node inspect-fig.mjs <arquivo.fig> <saida.json>');
}

const document = parseFig(new Uint8Array(readFileSync(figFile)));
const ignoredKeys = new Set(['blobs', 'vectorNetwork', 'imageData']);

function jsonValue(value) {
  if (value instanceof Uint8Array) return `[Uint8Array ${value.byteLength} bytes]`;
  return value;
}

function simplify(node) {
  const result = { id: nodeId(node) };
  for (const [key, value] of Object.entries(node)) {
    if (!ignoredKeys.has(key)) result[key] = jsonValue(value);
  }
  return result;
}

const nodes = document.nodes.map(simplify);
const typeCount = nodes.reduce((count, node) => {
  count[node.type ?? 'UNKNOWN'] = (count[node.type ?? 'UNKNOWN'] ?? 0) + 1;
  return count;
}, {});

writeFileSync(outputFile, JSON.stringify({
  header: document.header,
  meta: document.meta,
  nodeCount: nodes.length,
  typeCount,
  nodes
}, null, 2));

console.log(`Arquivo analisado: ${nodes.length} nós.`);
