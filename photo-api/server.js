import express from 'express';
import path from 'node:path';
import fs from 'node:fs';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const PORT = process.env.PORT || 3000;
// Chave privada exigida no header `x-api-key`. Troque em produção via .env.
const API_KEY = process.env.PHOTO_API_KEY || 'copa26-dev-key';
const PHOTOS_DIR = path.join(__dirname, 'photos');

const app = express();

/**
 * Health check público (sem auth) para readiness/monitoramento.
 * Registrado ANTES do middleware de auth para não exigir chave.
 */
app.get('/health', (_req, res) => res.json({ status: 'ok' }));

/** Middleware de autenticação: só responde a quem apresenta a chave privada. */
app.use((req, res, next) => {
  const key = req.header('x-api-key');
  if (key !== API_KEY) {
    return res.status(401).json({ error: 'Chave de API inválida ou ausente.' });
  }
  next();
});

// Extensões de imagem aceitas, em ordem de preferência, e seus content-types.
const IMG_EXTS = [
  ['png', 'image/png'],
  ['jpg', 'image/jpeg'],
  ['jpeg', 'image/jpeg'],
  ['webp', 'image/webp'],
];

/**
 * Serve a foto local `photos/<kind>/<id>.<ext>` (qualquer extensão suportada).
 * Quando não existe, redireciona (302) para um avatar de iniciais gerado a
 * partir do nome (`?name=`), servindo de fallback gracioso e mantendo a
 * autenticação apenas no nosso endpoint.
 */
function servePhoto(kind) {
  return (req, res) => {
    const id = String(req.params.id).replace(/[^0-9]/g, '');
    if (id) {
      for (const [ext, contentType] of IMG_EXTS) {
        const file = path.join(PHOTOS_DIR, kind, `${id}.${ext}`);
        if (fs.existsSync(file)) {
          res.type(contentType);
          res.set('Cache-Control', 'public, max-age=86400');
          return res.sendFile(file);
        }
      }
    }
    const name = (req.query.name || `#${id}`).toString();
    const fallback =
      `https://ui-avatars.com/api/?background=random&size=256&name=${encodeURIComponent(name)}`;
    return res.redirect(302, fallback);
  };
}

app.get('/players/:id', servePhoto('players'));
app.get('/coaches/:id', servePhoto('coaches'));

app.listen(PORT, () => {
  console.log(`Photo API ouvindo em http://localhost:${PORT}`);
  console.log(`Header exigido: x-api-key: ${API_KEY}`);
});
