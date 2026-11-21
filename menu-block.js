
var MM = { casino: { mode: "off", boost: 30, pay: 1 } };
try {
  var mmS = JSON.parse(localStorage.getItem("modmenu_cfg") || "null");
  if (mmS && mmS.casino) MM.casino = mmS.casino;
  if (mmS && mmS.garage > 0) MM.garage = mmS.garage | 0;
  if (mmS && mmS.p2p && typeof mmS.p2p === "object") MM.p2p = { room: String(mmS.p2p.room || "all").slice(0, 30), tracker: String(mmS.p2p.tracker || "").slice(0, 100), gifts: mmS.p2p.gifts !== false };
} catch (e) {}
function mmSave() {
  try {
    localStorage.setItem("modmenu_cfg", JSON.stringify(MM));
  } catch (e) {}
}
function mR(a, c) {
  var m = MM.casino;
  if (m.mode === "win") return Math.random() * c;
  if (m.mode === "lose") return c + Math.random() * (1 - c);
  if (m.mode === "boost" && Math.random() * 100 < m.boost) return Math.random() * c;
  return a;
}
function mP() {
  var v = +MM.casino.pay;
  return v > 0 ? v : 1;
}
var MM_MAX_MONEY = 9e14,
  MM_MAX_PUMP = 1e9;
  function mmWin() { try { return localStorage.getItem("modmenu_win") !== "0"; } catch (x) { return true; } }
  function mmCm(r, mode) {
    if (!mmWin() || !r) return r;
    if (mode === "plane") { r.land = true; if (r.m < 5) { r.m = 5; if (r.ev && r.ev.length) r.ev[r.ev.length - 1].m = 5; } }
    else if (r.m < 5) r.m = 5;
    return r;
  }
  function mmBest(pool) {
    if (!mmWin()) return lbDraw(pool);
    var b = pool[0]; pool.forEach(function (t) { if (t.v > b.v) b = t; }); return b;
  }

function mC() {
  if (!o) return;
  if (typeof o.balance != "number" || !isFinite(o.balance)) o.balance = 0;
  if (o.balance > MM_MAX_MONEY) o.balance = MM_MAX_MONEY;
  if (o.hwq && typeof o.hwq.p == "number" && o.hwq.p > MM_MAX_PUMP) o.hwq.p = MM_MAX_PUMP;
}
setTimeout(function () {
  try {
    mmInstall();
  } catch (e) {
    try {
      console.error("mod-menu:", e);
    } catch (x) {}
  }
}, 0);

function mmRef() {
  if (!o) return;
  try {
    Al();
  } catch (e) {}
  try {
    I(o.screen && o.screen !== "register" ? o.screen : "home");
  } catch (e) {}
  try {
    ht();
  } catch (e) {}
}
function mmSpin(bet, chance, win) {
  var m = Math.floor((f0 / chance) * 100) / 100,
    n = win ? Math.round(bet * m) : 0,
    net = n - bet;
  o.balance += net;
  o.ops.push({ ts: y(), text: net >= 0 ? "Выдано через мод-меню" : "Списано через мод-меню", amount: net });
  if (o.ops.length > 150) o.ops = o.ops.slice(-150);
  return net;
}
function mmCasino(amount) {
  if (!o) return 0;
  amount = Math.round(amount);
  var bal = Math.round(o.balance);
  if (amount > 0) amount = Math.min(amount, Math.max(0, MM_MAX_MONEY - bal));
  else amount = Math.max(amount, -Math.max(0, bal));
  if (!amount) return 0;
  var got = 0,
    guard = 0,
    ops0 = o.ops.length;
  if (amount < 0) {
    got = mmSpin(-amount, 0.5, false);
  } else {
    var rem = amount,
      m1 = Math.floor((f0 / 0.01) * 100) / 100 - 1,
      m2 = Math.floor((f0 / 0.95) * 100) / 100 - 1;
    while (rem > 0 && guard++ < 8) {
      var k = Math.min(MM_MAX_MONEY, Math.floor(rem / m1)),
        net = k >= 1 ? mmSpin(k, 0.01, true) : mmSpin(Math.max(1, Math.round(rem / m2)), 0.95, true);
      got += net;
      rem -= net;
    }
  }
  if (o.ops.length - ops0 > 1) {
    o.ops.splice(ops0, o.ops.length - ops0, { ts: y(), text: got >= 0 ? "Выдано через мод-меню" : "Списано через мод-меню", amount: got });
  }
  q();
  try {
    qt();
  } catch (e) {}
  try {
    T();
    _();
  } catch (e) {}
  try {
    mmRef();
  } catch (e) {}
  return got;
}
function mmMoney(d) {
  return mmCasino(d);
}
function mmTaxClear() {
  var t = _e(),
    n = 0;
  Object.keys(t.days).forEach(function (d) {
    var due = pl(d);
    if (due > 0) {
      t.paidAmt[d] = (t.paidAmt[d] || 0) + due;
      n++;
    }
  });
  q();
  try {
    Kn();
    Oi();
  } catch (e) {}
  try {
    mmRef();
  } catch (e) {}
  return n;
}
function mmGive(op) {
  if (!o) return [];
  var r = ze[op.mi];
  if (!r) return [];
  var n = r[5][op.gi] || r[5][0],
    l = r[6][op.ci] || r[6][0],
    out = [],
    k;
  for (k = 0; k < op.count; k++) {
    var u = {
      id: o.nextId++,
      brand: r[0],
      model: r[1],
      year: r[2],
      style: r[3],
      pro: r[4],
      gb: n[0],
      color: ke[l][0],
      hex: ke[l][1],
      cond: op.cond,
      bat: op.bat,
      kit: op.kit,
      paid: 0,
      boughtAt: y(),
    };
    if (op.name) u.cn = op.name;
    if (op.price > 0) u.mp = Math.round(op.price);
    u.paid = op.paid >= 0 && op.paid != null ? Math.round(op.paid) : bt(u);
    o.phones.push(u);
    out.push(u);
  }
  q();
  mmRef();
  return out;
}

function mmHasCars() {
  return typeof AR_MODELS !== "undefined" && typeof arState === "function";
}
function mmArRefresh() {
  q();
  try {
    arRender();
  } catch (e) {}
}
function mmGiveCar(op) {
  if (!mmHasCars() || !o) return null;
  var st = arState(),
    m = arModel(op.m);
  if (!m) return null;
  var car = arGen({ m: op.m, kind: "plain", noTune: true }),
    col = AR_COLORS[op.col] || AR_COLORS[0],
    cur = arYear();
  car.y = Math.max(m[6], Math.min(op.y || m[7], Math.max(m[7], cur)));
  car.km = Math.max(0, Math.min(999000, Math.round(op.km)));
  car.col = col[0];
  car.hex = col[1];
  car.hid = null;
  car.diag = true;
  car.kind = "plain";
  if (op.cond > 0) AR_CK.forEach(function (k) { car.cond[k] = Math.max(1, Math.min(100, Math.round(op.cond))); });
  car.city = o.city;
  car.paid = 0;
  car.cost = {};
  car.boughtAt = y();
  car.idleAt = y();
  car.st = "garage";
  car.deals = [{ ts: y(), k: "buy", p: 0, w: "Мод-меню" }];
  delete car.min;
  delete car.deal;
  delete car.haggled;
  if (st.own.length >= AR_CFG.garage) AR_CFG.garage = st.own.length + 1;
  st.own.push(car);
  mmArRefresh();
  return car;
}
function mmCarsFix() {
  var n = 0;
  arState().own.forEach(function (c) {
    AR_CK.forEach(function (k) { c.cond[k] = 100; });
    c.hid = null;
    c.diag = true;
    n++;
  });
  mmArRefresh();
  return n;
}
function mmCarsDone() {
  var n = 0;
  arState().own.forEach(function (c) {
    if (c.job) {
      c.job.done = 0;
      n++;
    }
  });
  try {
    arTick(y());
  } catch (e) {}
  mmArRefresh();
  return n;
}

function mmMove(city) {
  if (!o || !Ha(city)) return false;
  o.travel = { to: city, end: y() };
  ku();
  q();
  try {
    mmRef();
  } catch (e) {}
  return true;
}
function mmCredit(score) {
  var c = Fa();
  score = Math.max(Wa, Math.min(Rn, Math.round(score)));
  c.score = score;
  q();
  try {
    Oi();
  } catch (e) {}
  return score;
}

function mmListing(op) {
  if (!o) return null;
  var r = ze[op.mi];
  if (!r) return null;
  var g = r[5][op.gi] || r[5][0],
    k = r[6][op.ci] || r[6][0],
    price = Math.max(1, Math.round(op.price)),
    d = {
      id: o.nextId++,
      brand: r[0],
      model: r[1],
      year: r[2],
      style: r[3],
      pro: r[4],
      gb: g[0],
      color: ke[k][0],
      hex: ke[k][1],
      cond: op.cond,
      bat: op.bat,
      kit: op.kit,
      city: ti(),
      price: price,
      fair: op.fair > 0 ? Math.round(op.fair) : price,
      desc: op.desc || "",
      seller: { name: op.seller || "Частник", rating: op.rating || 4.8, reviews: op.reviews | 0 },
      ts: y(),
      mod: 1,
    };
  if (op.lock === "pass" || op.lock === "frp") {
    d.lock = op.lock;
    d.icl = false;
  }
  if (op.name) d.cn = op.name;
  o.listings.unshift(d);
  q();
  try {
    te();
    Vt();
  } catch (e) {}
  mmRef();
  return d;
}

function mmNumTiers() {
  return [We].concat(is.slice().reverse(), [Jl]);
}
function mmNumTier(e) {
  var i = 1,
    a = 1,
    r;
  for (r = 1; r < e.length; r++) {
    i = e[r] === e[r - 1] ? i + 1 : 1;
    if (i > a) a = i;
  }
  var n = We;
  for (r = 0; r < is.length; r++)
    if (a >= is[r].min) {
      n = is[r];
      break;
    }
  if (e === e.split("").reverse().join("") && Jl.v > n.v) n = Jl;
  return n;
}
function mmRd(n) {
  var t = "";
  while (n-- > 0) t += Math.floor(Math.random() * 10);
  return t;
}
function mmNumRand(tier) {
  var best = "9" + mmRd(9),
    k;
  for (k = 0; k < 4000; k++) {
    var e = mmRd(7);
    if (tier === Jl) e = e.slice(0, 4) + e.slice(0, 3).split("").reverse().join("");
    else if (tier.min) {
      var L = tier.min,
        pos = Math.floor(Math.random() * (8 - L)),
        d = String(Math.floor(Math.random() * 10));
      e = e.slice(0, pos) + new Array(L + 1).join(d) + e.slice(pos + L);
      e = e.slice(0, 7);
    }
    var cand = "9" + mmRd(2) + e;
    if (mmNumTier(cand.slice(3)) === tier) return cand;
    best = cand;
  }
  return best;
}
function mmNumGive(op) {
  if (!o) return [];
  var st = Qr(),
    out = [],
    k;
  function add(digits, t) {
    var rec = { n: _l(digits), v: op.price > 0 ? Math.round(op.price) : t.v, r: t.name, c: t.c, id: st.id++ };
    st.list.push(rec);
    out.push(rec);
  }
  if (op.digits) add(op.digits, op.tier || mmNumTier(op.digits.slice(3)));
  else
    for (k = 0; k < op.count; k++) {
      if (op.tier) add(mmNumRand(op.tier), op.tier);
      else {
        var w = Wu();
        add(w.n.replace(/\D/g, "").slice(1), { v: w.v, name: w.r, c: w.c });
      }
    }
  q();
  try {
    rs();
  } catch (e) {}
  return out;
}

function mmInstall() {
  try {
    if (MM.garage > 0 && mmHasCars()) AR_CFG.garage = MM.garage;
  } catch (e) {}
  if (document.getElementById("mm-fab")) return;
  var D = document;
  function $(id) {
    return D.getElementById(id);
  }
  function esc(t) {
    return String(t == null ? "" : t).replace(/[&<>"']/g, function (c) {
      return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c];
    });
  }
  function num(v, d) {
    v = parseFloat(String(v).replace(/\s/g, "").replace(",", "."));
    return isFinite(v) ? v : d;
  }
  function money(v) {
    return Math.round(v).toLocaleString("ru-RU") + " ₽";
  }
  function toast(t) {
    try {
      L(t);
    } catch (e) {}
  }

  var css =
    "#mm-fab{position:fixed;right:12px;bottom:110px;width:46px;height:46px;border-radius:50%;border:0;background:#7c3aed;color:#fff;font-size:21px;line-height:46px;padding:0;z-index:99998;box-shadow:0 4px 14px rgba(0,0,0,.4);opacity:.88;touch-action:none;user-select:none;-webkit-user-select:none}" +
    "#mm-panel{position:fixed;left:50%;bottom:0;transform:translateX(-50%);width:100%;max-width:560px;height:78%;background:var(--surface,#fff);color:var(--ink,#111);border-radius:18px 18px 0 0;z-index:99999;display:none;flex-direction:column;box-shadow:0 -10px 34px rgba(0,0,0,.4);font-size:14px}" +
    "#mm-panel.on{display:flex}" +
    ".mm-head{display:flex;align-items:center;justify-content:space-between;padding:12px 14px 4px;font-weight:800;font-size:16px}" +
    ".mm-x{border:0;background:var(--bg,#eee);color:inherit;width:32px;height:32px;border-radius:50%;font-size:16px}" +
    ".mm-tabs{display:flex;gap:6px;overflow-x:auto;padding:6px 12px 8px;flex:none}" +
    ".mm-tab{flex:none;border:0;border-radius:999px;padding:8px 13px;background:var(--bg,#eee);color:inherit;font-weight:600;font-size:13px}" +
    ".mm-tab.on{background:#7c3aed;color:#fff}" +
    ".mm-card .mm-muted,.mm-body>.mm-muted{display:none}.mm-keep{font-size:14px;margin-bottom:6px}.mm-zay{font-size:12px;font-weight:600;opacity:.75;text-align:center;padding:10px 0 2px}" +
    ".mm-body{flex:1;overflow-y:auto;padding:4px 14px calc(18px + env(safe-area-inset-bottom,0px));-webkit-overflow-scrolling:touch}" +
    ".mm-card{background:var(--bg,#f2f3f5);border-radius:14px;padding:12px;margin:0 0 10px}" +
    ".mm-card h4{margin:0 0 8px;font-size:14px}" +
    ".mm-row{display:flex;gap:6px;flex-wrap:wrap;align-items:center;margin:6px 0}" +
    ".mm-row>label{flex:1 1 100%;font-size:12px;color:var(--muted,#888)}" +
    ".mm-btn{border:0;border-radius:10px;padding:9px 12px;background:#7c3aed;color:#fff;font-weight:700;font-size:13px}" +
    ".mm-btn.alt{background:var(--surface,#fff);color:var(--ink,#111);border:1px solid var(--line,#ddd)}" +
    ".mm-btn.red{background:#e0393e}" +
    ".mm-in,.mm-sel{flex:1 1 120px;min-width:0;border:1px solid var(--line,#ddd);border-radius:10px;padding:9px 10px;background:var(--surface,#fff);color:inherit;font-size:14px}" +
    ".mm-muted{color:var(--muted,#888);font-size:12px;line-height:1.4}" +
    ".mm-big{font-size:20px;font-weight:800}" +
    ".mm-ph{display:flex;align-items:center;justify-content:space-between;gap:8px;padding:8px 0;border-bottom:1px solid var(--line,#ddd)}" +
    ".mm-ph b{display:block;font-size:13px}" +
    ".mm-opt{display:flex;align-items:center;gap:8px;padding:6px 0}" +
    ".mm-wm{text-align:center;font-size:11px;color:var(--muted,#888);padding:6px 0 0;letter-spacing:.3px}" +
    ".mm-head small{display:block;font-weight:600;font-size:11px;color:var(--muted,#888)}" +
    "#mm-panel.mm-img .mm-head,#mm-panel.mm-img .mm-tabs{background:color-mix(in srgb,var(--surface,#fff) 80%,transparent)}" +
    "#mm-panel.mm-img .mm-card{background:color-mix(in srgb,var(--bg,#f2f3f5) 88%,transparent)}" +
    "#mm-panel.mm-img .mm-body>.mm-muted,#mm-panel.mm-img .mm-wm{background:color-mix(in srgb,var(--surface,#fff) 80%,transparent);border-radius:8px;padding:4px 8px}" +
    ".mm-pv{width:64px;height:64px;border-radius:12px;background:var(--bg,#eee) center/cover;border:1px solid var(--line,#ddd);flex:none}" +
    ".mm-ta{width:100%;min-height:90px;border:1px solid var(--line,#ddd);border-radius:10px;padding:8px;background:var(--surface,#fff);color:inherit;font:11px/1.35 monospace;box-sizing:border-box}";
  var st = D.createElement("style");
  st.id = "mm-css";
  st.textContent = css;
  D.head.appendChild(st);

  var fab = D.createElement("button");
  fab.id = "mm-fab";
  fab.type = "button";
  fab.textContent = "🛠";
  fab.setAttribute("aria-label", "Мод-меню");
  D.body.appendChild(fab);
  var panel = D.createElement("div");
  panel.id = "mm-panel";
  panel.innerHTML =
    '<div class="mm-head"><span>🛠 Мод-меню<small>by TG Sync_sm</small></span><button class="mm-x" type="button" id="mm-close">✕</button></div>' +
    '<div class="mm-tabs" id="mm-tabs"></div><div class="mm-body" id="mm-body"></div>';
  D.body.appendChild(panel);

  try {
    var pos = JSON.parse(localStorage.getItem("modmenu_pos") || "null");
    if (pos) {
      fab.style.right = "auto";
      fab.style.bottom = "auto";
      fab.style.left = Math.min(pos.x, innerWidth - 50) + "px";
      fab.style.top = Math.min(pos.y, innerHeight - 50) + "px";
    }
  } catch (e) {}
  (function () {
    var sx,
      sy,
      ox,
      oy,
      moved = false,
      down = false;
    fab.addEventListener("pointerdown", function (e) {
      down = true;
      moved = false;
      sx = e.clientX;
      sy = e.clientY;
      var r = fab.getBoundingClientRect();
      ox = r.left;
      oy = r.top;
      try {
        fab.setPointerCapture(e.pointerId);
      } catch (x) {}
    });
    fab.addEventListener("pointermove", function (e) {
      if (!down) return;
      var dx = e.clientX - sx,
        dy = e.clientY - sy;
      if (!moved && Math.abs(dx) + Math.abs(dy) < 8) return;
      moved = true;
      fab.style.right = "auto";
      fab.style.bottom = "auto";
      fab.style.left = Math.max(0, Math.min(innerWidth - 46, ox + dx)) + "px";
      fab.style.top = Math.max(0, Math.min(innerHeight - 46, oy + dy)) + "px";
    });
    fab.addEventListener("pointerup", function () {
      down = false;
      if (moved) {
        try {
          localStorage.setItem("modmenu_pos", JSON.stringify({ x: parseFloat(fab.style.left), y: parseFloat(fab.style.top) }));
        } catch (e) {}
      } else toggle();
    });
  })();

  var TABS = [
    ["player", "Игрок"],
    ["casino", "Казино"],
    ["give", "Выдать телефон"],
    ["stock", "Мои телефоны"],
    ["nums", "Номера"],
    ["list", "Объявления"],
    ["tm", "Ещё"],
    ["cars", "Авто"],
    ["look", "Вид"],
    ["serv", "Сервис"],
    ["data", "Данные"],
  ];
  var cur = "player",
    editId = null,
    tabsEl = $("mm-tabs"),
    bodyEl = $("mm-body");

  function toggle(on) {
    var open = on == null ? !panel.classList.contains("on") : on;
    panel.classList.toggle("on", open);
    if (open) render();
  }
  $("mm-close").addEventListener("click", function () {
    toggle(false);
  });
  D.addEventListener("keydown", function (e) {
    if (e.key === "`" || e.key === "ё") {
      var t = e.target && e.target.tagName;
      if (t === "INPUT" || t === "TEXTAREA" || t === "SELECT") return;
      toggle();
    }
  });

  function drawTabs() {
    tabsEl.innerHTML = TABS.map(function (t) {
      return '<button type="button" class="mm-tab' + (t[0] === cur ? " on" : "") + '" data-tab="' + t[0] + '">' + t[1] + "</button>";
    }).join("");
  }
  tabsEl.addEventListener("click", function (e) {
    var b = e.target.closest("[data-tab]");
    if (b) {
      cur = b.dataset.tab;
      editId = null;
      render();
    }
  });

  function vPlayer() {
    var h = '<div class="mm-card"><h4>Деньги — через казино</h4><div class="mm-keep">Баланс: <b id="mm-bal">' + money(o.balance) + '</b></div><div class="mm-row">';
    [1e4, 1e5, 1e6, 1e7, 1e8].forEach(function (v) {
      h += '<button class="mm-btn" data-a="cash" data-v="' + v + '">+' + v.toLocaleString("ru-RU") + "</button>";
    });
    h +=
      '<button class="mm-btn" data-a="cash" data-v="' + MM_MAX_MONEY + '">До лимита</button></div>' +
      '<div class="mm-row"><input class="mm-in" id="mm-amt" type="text" inputmode="decimal" placeholder="Сумма, ₽ (со знаком «−» — проигрыш)">' +
      '<button class="mm-btn" data-a="cashcustom">Выдать</button>' +
      '<button class="mm-btn alt" data-a="cashset">Поставить баланс</button></div>' +
      '<div class="mm-muted">Больше лимита баланс не растёт.</div></div>';

    h +=
      '<div class="mm-card"><h4>Репутация: <span id="mm-repv">' +
      Math.round(o.rep == null ? 50 : o.rep) +
      '</span></h4><input type="range" min="0" max="100" step="1" id="mm-rep" style="width:100%" value="' +
      Math.round(o.rep == null ? 50 : o.rep) +
      '"></div>';

    o.upg = o.upg || { d: 0, r: 0, b: 0, s: 0 };
    h += '<div class="mm-card"><h4>Улучшения</h4>';
    Ul.forEach(function (u) {
      h += '<div class="mm-row"><label>' + esc(u.name) + '</label><select class="mm-sel" data-a="upg" data-k="' + u.id + '">';
      for (var i = 0; i <= 5; i++) h += '<option value="' + i + '"' + ((o.upg[u.id] | 0) === i ? " selected" : "") + ">Уровень " + i + "</option>";
      h += "</select></div>";
    });
    h += "</div>";

    h +=
      '<div class="mm-card"><h4>Сделки: <span id="mm-deals">' + (o.deals | 0).toLocaleString("ru-RU") + '</span></h4><div class="mm-row">' +
      [10, 100, 1000, 10000].map(function (v) {
        return '<button class="mm-btn" data-a="deals" data-v="' + v + '">+' + v.toLocaleString("ru-RU") + "</button>";
      }).join("") +
      '</div><div class="mm-row"><input class="mm-in" id="mm-dn" type="text" inputmode="numeric" placeholder="Точное число сделок"><button class="mm-btn alt" data-a="dealsset">Поставить</button></div>' +
      '<div class="mm-muted">Меняет только счётчик сделок (до 1 000 000): он влияет на место в вкладке «Сделки» локального рейтинга. Достижения и задания не затрагиваются.</div></div>';

    var cr = Fa();
    h +=
      '<div class="mm-card"><h4>Кредитный рейтинг в банке: <span id="mm-crv">' + cr.score + "</span></h4>" +
      '<input type="range" min="' + Wa + '" max="' + Rn + '" step="5" id="mm-cr" style="width:100%" value="' + cr.score + '">' +
      '<div class="mm-muted">От ' + Wa + " до " + Rn + ". От него зависит лимит кредита и ставка. Сейчас: " + K0(cr.score) + ".</div></div>";
    h += '<div class="mm-card"><h4>Переезд. Сейчас: ' + esc(ti()) + '</h4><div class="mm-row"><select class="mm-sel" id="mm-city">' +
      Ga.map(function (c) {
        return '<option value="' + esc(c[0]) + '"' + (c[0] === ti() ? " selected" : "") + ">" + esc(c[0]) + " · цены ×" + c[3] + "</option>";
      }).join("") +
      '</select><button class="mm-btn" data-a="move">Переехать</button></div><div class="mm-muted">Мгновенно, без поездки: обновляется лента и цены города.</div></div>';

    var hw = null;
    try {
      hw = hwState();
    } catch (e) {}

    h += '<div class="mm-card"><h4>Прочее</h4><div class="mm-row">';
    if (o.loan) h += '<button class="mm-btn alt" data-a="loan0">Списать кредит</button>';
    h +=
      '<button class="mm-btn alt" data-a="repfin">Завершить ремонты</button><button class="mm-btn alt" data-a="tripfin">Завершить поездку</button>' +
      '<button class="mm-btn alt" data-a="taxclear">Погасить налоговые долги</button><button class="mm-btn alt" data-a="unblock">Снять блокировки поддержки</button>' +
      '<button class="mm-btn alt" data-a="repmax">Репутация 100</button><button class="mm-btn alt" data-a="upgmax">Все улучшения на 5</button></div></div>';
    return h;
  }

  function vCasino() {
    var c = MM.casino;
    function radio(v, t, d) {
      return (
        '<label class="mm-opt"><input type="radio" name="mm-cm" data-a="cmode" value="' +
        v +
        '"' +
        (c.mode === v ? " checked" : "") +
        '><span><b>' +
        t +
        '</b><br><span class="mm-muted">' +
        d +
        "</span></span></label>"
      );
    }
    var h =
      '<div class="mm-card"><h4>Режим броска</h4>' +
      radio("off", "Честно", "Как в оригинале.") +
      radio("win", "Всегда выигрыш", "Каждая ставка заходит, при любом выбранном шансе.") +
      radio("lose", "Всегда проигрыш", "Для проверки экрана проигрыша.") +
      radio("boost", "Подкрутка шанса", "С указанной вероятностью бросок принудительно выигрышный; иначе обычный.") +
      '<div class="mm-row"><label>Сила подкрутки: <b id="mm-bv">' +
      c.boost +
      '%</b></label><input type="range" id="mm-boost" min="0" max="100" step="5" style="width:100%" value="' +
      c.boost +
      '"></div></div>';
    h +=
      '<div class="mm-card"><h4>Множитель выплаты</h4><select class="mm-sel" id="mm-pay">' +
      [1, 2, 3, 5, 10, 50, 100]
        .map(function (v) {
          return '<option value="' + v + '"' + (+c.pay === v ? " selected" : "") + ">×" + v + "</option>";
        })
        .join("") +
      '</select><div class="mm-muted" style="margin-top:6px">Умножает выигрыш при победе. Шансы на экране казино не меняются: подкручивается сам бросок.</div></div>';
    h += '<div class="mm-row"><button class="mm-btn" data-a="opencasino">Открыть казино</button></div>';
    return h;
  }

  function modelOptions(q) {
    q = (q || "").toLowerCase();
    var h = "";
    ze.forEach(function (r, i) {
      var t = r[0] + " · " + r[1] + " (" + r[2] + ")";
      if (!q || t.toLowerCase().indexOf(q) > -1) h += '<option value="' + i + '">' + esc(t) + "</option>";
    });
    return h;
  }
  function subOptions(mi) {
    var r = ze[mi] || ze[0];
    return {
      gb: r[5]
        .map(function (g, i) {
          return '<option value="' + i + '">' + g[0] + " ГБ — " + money(g[1]) + "</option>";
        })
        .join(""),
      col: r[6]
        .map(function (k, i) {
          return '<option value="' + i + '">' + esc(ke[k][0]) + "</option>";
        })
        .join(""),
    };
  }
  function vGive() {
    var m0 = subOptions(0);
    return (
      '<div class="mm-card"><h4>Каталог</h4>' +
      '<div class="mm-row"><input class="mm-in" id="mm-q" type="text" placeholder="Поиск модели…"></div>' +
      '<div class="mm-row"><label>Модель</label><select class="mm-sel" id="mm-m" style="flex-basis:100%">' +
      modelOptions("") +
      "</select></div>" +
      '<div class="mm-row"><label>Память</label><select class="mm-sel" id="mm-g">' +
      m0.gb +
      '</select><label>Цвет</label><select class="mm-sel" id="mm-c">' +
      m0.col +
      "</select></div>" +
      '<div class="mm-row"><label>Состояние</label><select class="mm-sel" id="mm-cond">' +
      ft
        .map(function (c, i) {
          return '<option value="' + i + '">' + esc(c[0]) + "</option>";
        })
        .join("") +
      '</select><label>Комплект</label><select class="mm-sel" id="mm-kit">' +
      $s
        .map(function (k, i) {
          return '<option value="' + esc(k) + '"' + (i === $s.length - 1 ? " selected" : "") + ">" + esc(k) + "</option>";
        })
        .join("") +
      "</select></div>" +
      '<div class="mm-row"><label>Батарея, %</label><input class="mm-in" id="mm-bat" type="number" min="1" max="100" value="100"><label>Количество</label><input class="mm-in" id="mm-cnt" type="number" min="1" max="50" value="1"></div></div>' +
      '<div class="mm-card"><h4>Свои параметры (необязательно)</h4>' +
      '<div class="mm-row"><label>Своё название</label><input class="mm-in" id="mm-name" type="text" maxlength="60" placeholder="напр. «iPhone 99 Ultra»" style="flex-basis:100%"></div>' +
      '<div class="mm-row"><label>Рыночная цена, ₽ (за сколько он продаётся)</label><input class="mm-in" id="mm-price" type="text" inputmode="numeric" placeholder="по каталогу" style="flex-basis:100%"></div>' +
      '<div class="mm-row"><label>«Куплен за», ₽ (для учёта прибыли)</label><input class="mm-in" id="mm-paid" type="text" inputmode="numeric" placeholder="0 = бесплатно" style="flex-basis:100%"></div>' +
      '<div class="mm-muted">Если оставить цену пустой, оценка берётся из каталога по модели, памяти и состоянию.</div></div>' +
      '<div class="mm-row"><button class="mm-btn" data-a="give" style="flex:1">Выдать на склад</button></div>'
    );
  }

  function vStock() {
    var h = "";
    if (!o.phones.length) return '<div class="mm-muted">На складе пусто.</div>';
    var list = o.phones.slice().reverse().slice(0, 80);
    list.forEach(function (p) {
      var price = 0;
      try {
        price = bt(p);
      } catch (e) {}
      h +=
        '<div class="mm-ph"><div><b>' +
        esc(F(p)) +
        '</b><span class="mm-muted">' +
        esc(ft[p.cond] ? ft[p.cond][0] : "") +
        " · " +
        (p.bat || "—") +
        "% · оценка " +
        money(price) +
        (p.mp ? " (фикс.)" : "") +
        '</span></div><button class="mm-btn alt" data-a="edit" data-id="' +
        p.id +
        '">Изменить</button></div>';
      if (editId === p.id)
        h +=
          '<div class="mm-card" style="margin-top:6px"><div class="mm-row"><label>Название (пусто = как в каталоге)</label><input class="mm-in" id="mm-en" type="text" maxlength="60" value="' +
          esc(p.cn || "") +
          '" style="flex-basis:100%"></div>' +
          '<div class="mm-row"><label>Рыночная цена, ₽ (пусто = по каталогу)</label><input class="mm-in" id="mm-ep" type="text" inputmode="numeric" value="' +
          (p.mp || "") +
          '" style="flex-basis:100%"></div>' +
          '<div class="mm-row"><label>Состояние</label><select class="mm-sel" id="mm-ec">' +
          ft
            .map(function (c, i) {
              return '<option value="' + i + '"' + (p.cond === i ? " selected" : "") + ">" + esc(c[0]) + "</option>";
            })
            .join("") +
          '</select><label>Батарея, %</label><input class="mm-in" id="mm-eb" type="number" min="1" max="100" value="' +
          (p.bat || 100) +
          '"></div><div class="mm-row"><button class="mm-btn" data-a="esave" data-id="' +
          p.id +
          '">Сохранить</button><button class="mm-btn red" data-a="edel" data-id="' +
          p.id +
          '">Удалить телефон</button></div></div>';
    });
    if (o.phones.length > 80) h += '<div class="mm-muted" style="padding-top:8px">Показаны последние 80 из ' + o.phones.length + ".</div>";
    return h;
  }

  function vData() {
    return (
      '<div class="mm-card"><h4>Экспорт сохранения</h4><div class="mm-row"><button class="mm-btn alt" data-a="export">Показать JSON</button></div>' +
      '<textarea class="mm-ta" id="mm-exp" readonly placeholder="Здесь появится сохранение. Выделите и скопируйте."></textarea></div>' +
      '<div class="mm-card"><h4>Импорт сохранения</h4><textarea class="mm-ta" id="mm-imp" placeholder="Вставьте JSON сохранения"></textarea>' +
      '<div class="mm-row"><button class="mm-btn" data-a="import">Загрузить и перезапустить</button></div>' +
      '<div class="mm-muted">Текущее сохранение при этом заменяется. Перед импортом сделайте экспорт.</div></div>' +
      '<div class="mm-card"><h4>Мод-меню</h4><div class="mm-row"><button class="mm-btn alt" data-a="resetmod">Сбросить настройки меню</button></div>' +
      '<div class="mm-muted">Кнопку 🛠 можно перетаскивать. На компьютере меню открывается клавишей ` (Ё).</div></div>'
    );
  }


  function vCars() {
    if (!mmHasCars()) return '<div class="mm-card"><div class="mm-muted">В этой версии игры нет Авторынка.</div></div>';
    var st = arState(),
      mods = AR_MODELS.map(function (m, i) {
        return '<option value="' + esc(m[0]) + '">' + esc(m[1] + " " + m[2]) + " · " + m[6] + "–" + m[7] + "</option>";
      }).join(""),
      cols = AR_COLORS.map(function (c, i) {
        return '<option value="' + i + '">' + esc(c[0]) + "</option>";
      }).join("");
    var h =
      '<div class="mm-card"><h4>Выдать машину в гараж</h4>' +
      '<div class="mm-row"><select class="mm-sel" id="mm-cm" style="flex-basis:100%">' + mods + "</select></div>" +
      '<div class="mm-row"><label>Год</label><input class="mm-in" id="mm-cy" type="number" placeholder="новейший"><label>Пробег, км</label><input class="mm-in" id="mm-ckm" type="number" value="0"></div>' +
      '<div class="mm-row"><label>Цвет</label><select class="mm-sel" id="mm-cc">' + cols + '</select><label>Состояние</label><select class="mm-sel" id="mm-cq"><option value="100">Идеальное</option><option value="80">Хорошее</option><option value="50">Среднее</option><option value="0">Как сгенерировано</option></select></div>' +
      '<div class="mm-row"><button class="mm-btn" data-a="cargive" style="flex:1">В гараж</button></div>' +
      '<div class="mm-muted">Машина приходит бесплатно (в журнал денег ничего не пишется). Если гараж полон, он расширяется.</div></div>';
    h +=
      '<div class="mm-card"><h4>Гараж: ' + st.own.length + " / " + AR_CFG.garage + '</h4><input type="range" min="5" max="30" step="1" id="mm-gar" style="width:100%" value="' + AR_CFG.garage + '">' +
      '<div class="mm-row"><button class="mm-btn alt" data-a="carfix">Починить все машины</button><button class="mm-btn alt" data-a="cardone">Завершить ремонт, тюнинг, доставку</button></div></div>';
    h +=
      '<div class="mm-card"><h4>Рынок</h4><div class="mm-row"><button class="mm-btn alt" data-a="carmkt">Обновить авторынок</button><button class="mm-btn alt" data-a="carbm">Обновить чёрный рынок</button></div>' +
      '<div class="mm-muted">Деньги на покупку машин выдаются на вкладке «Игрок»: баланс общий. После действий откройте приложение «Авторынок» заново, если экран не обновился.</div></div>';
    return h;
  }

  var P2P_TRACKERS = ["wss://tracker.openwebtorrent.com", "wss://tracker.webtorrent.dev", "wss://tracker.btorrent.xyz"];
  var P2P_MAX = 8; /* связей одновременно */
  var P2P = { id: Math.random().toString(36).slice(2, 10), conns: [], peers: {}, chat: [], seen: {}, escrow: {}, done: {}, offers: {}, socks: [], timer: 0, status: "off", gifts: 0, giftAt: {}, hash: "", pid: "" };
  var RTC_CFG = { iceServers: [{ urls: "stun:stun.l.google.com:19302" }] };
  function p2pCfg() {
    var c = MM.p2p || (MM.p2p = {});
    if (typeof c.room !== "string" || !c.room) c.room = "all";
    if (typeof c.tracker !== "string") c.tracker = "";
    if (typeof c.gifts !== "boolean") c.gifts = true;
    return c;
  }
  function p2pNum(v, max) {
    v = +v;
    return isFinite(v) ? Math.max(0, Math.min(max, v)) : 0;
  }
  function p2pStr(v, n) {
    return String(v == null ? "" : v).replace(/[\u0000-\u001f\u007f<>]/g, "").slice(0, n);
  }
  function p2pMid() {
    return Math.random().toString(36).slice(2, 12);
  }
  function p2pRand(n) {
    var s = "",
      a = "abcdefghijklmnopqrstuvwxyz0123456789";
    while (s.length < n) s += a[Math.floor(Math.random() * a.length)];
    return s;
  }
  function p2pWorth() {
    var t = Math.round(o.balance);
    (o.phones || []).forEach(function (p) {
      t += +p.paid || 0;
    });
    return t;
  }
  function p2pProfile() {
    return { id: P2P.id, nick: p2pStr(o.nick, 16), city: p2pStr(o.city, 24), worth: p2pWorth(), deals: o.deals | 0 };
  }
  function p2pSend(c, m) {
    try {
      if (c.dc && c.dc.readyState === "open") c.dc.send(JSON.stringify(m));
    } catch (e) {}
  }
  function p2pBroadcast(m, except) {
    m.mid = m.mid || p2pMid();
    P2P.seen[m.mid] = Date.now();
    P2P.conns.forEach(function (c) {
      if (c !== except) p2pSend(c, m);
    });
  }
  function p2pWaitIce(pc) {
    return new Promise(function (res) {
      if (pc.iceGatheringState === "complete") return res();
      var t = setTimeout(res, 3500);
      pc.addEventListener("icegatheringstatechange", function () {
        if (pc.iceGatheringState === "complete") {
          clearTimeout(t);
          res();
        }
      });
    });
  }
  function p2pDrop(c) {
    var i = P2P.conns.indexOf(c);
    if (i > -1) P2P.conns.splice(i, 1);
    if (c.pid && P2P.byPid && P2P.byPid[c.pid] === c) delete P2P.byPid[c.pid];
    try {
      c.pc.close();
    } catch (e) {}
    p2pPaint();
  }
  function p2pNewConn(pc, remote) {
    var c = { pc: pc, dc: null, open: false, pid: remote || "", rate: [], pr: "" };
    pc.onconnectionstatechange = function () {
      if (pc.connectionState === "failed" || pc.connectionState === "closed" || pc.connectionState === "disconnected") p2pDrop(c);
    };
    return c;
  }
  function p2pWire(c, dc) {
    c.dc = dc;
    dc.onopen = function () {
      P2P.byPid = P2P.byPid || {};
      if ((c.pid && P2P.byPid[c.pid] && P2P.byPid[c.pid] !== c) || P2P.conns.length >= P2P_MAX) return p2pDrop(c); /* уже связаны с этим игроком или нет места */
      if (c.pid) P2P.byPid[c.pid] = c;
      c.open = true;
      P2P.conns.push(c);
      p2pBroadcast({ t: "prof", p: p2pProfile() });
      p2pPaint();
    };
    dc.onclose = function () {
      p2pDrop(c);
    };
    dc.onmessage = function (e) {
      p2pOnMsg(c, e.data);
    };
  }
  function p2pSha1(str) {
    return crypto.subtle.digest("SHA-1", new TextEncoder().encode(str)).then(function (b) {
      var u = new Uint8Array(b),
        s = "";
      for (var i = 0; i < u.length; i++) s += String.fromCharCode(u[i]);
      return s;
    });
  }
  async function p2pMakeOffers(n) {
    var list = [];
    for (var i = 0; i < n; i++) {
      var pc = new RTCPeerConnection(RTC_CFG),
        c = p2pNewConn(pc, ""),
        oid = p2pRand(20);
      p2pWire(c, pc.createDataChannel("mod"));
      await pc.setLocalDescription(await pc.createOffer());
      await p2pWaitIce(pc);
      P2P.offers[oid] = { c: c, at: Date.now() };
      list.push({ offer_id: oid, offer: { type: "offer", sdp: pc.localDescription.sdp } });
    }
    return list;
  }
  async function p2pAnnounce() {
    if (P2P.status === "off") return;
    var now = Date.now();
    Object.keys(P2P.offers).forEach(function (k) {
      if (now - P2P.offers[k].at > 120000) {
        var cc = P2P.offers[k].c;
        delete P2P.offers[k];
        if (!cc.open) {
          try {
            cc.pc.close();
          } catch (e) {}
        }
      }
    });
    var room = p2pCfg().room,
      free = Math.max(0, P2P_MAX - P2P.conns.length);
    var offers = free ? await p2pMakeOffers(Math.min(4, free)) : [];
    P2P.socks.forEach(function (s) {
      if (s.ws.readyState !== 1) return;
      try {
        s.ws.send(JSON.stringify({ action: "announce", info_hash: P2P.hash, peer_id: P2P.pid, numwant: offers.length, uploaded: 0, downloaded: 0, left: 0, event: "started", offers: offers }));
      } catch (e) {}
    });
  }
  async function p2pOnTracker(s, d) {
    if (!d || d.info_hash !== P2P.hash || P2P.status === "off") return;
    if (typeof d.peer_id === "string" && d.peer_id === P2P.pid) return;
    if (d.offer && d.offer.sdp && typeof d.offer.sdp === "string" && d.offer.sdp.length < 12000 && typeof d.offer_id === "string" && typeof d.peer_id === "string") {
      if (P2P.conns.length >= P2P_MAX || (P2P.byPid && P2P.byPid[d.peer_id])) return;
      var pc = new RTCPeerConnection(RTC_CFG),
        c = p2pNewConn(pc, d.peer_id);
      pc.ondatachannel = function (e) {
        p2pWire(c, e.channel);
      };
      try {
        await pc.setRemoteDescription({ type: "offer", sdp: d.offer.sdp });
        await pc.setLocalDescription(await pc.createAnswer());
        await p2pWaitIce(pc);
        s.ws.send(JSON.stringify({ action: "announce", info_hash: P2P.hash, peer_id: P2P.pid, to_peer_id: d.peer_id, offer_id: d.offer_id, answer: { type: "answer", sdp: pc.localDescription.sdp } }));
      } catch (e) {
        try {
          pc.close();
        } catch (x) {}
      }
    } else if (d.answer && d.answer.sdp && typeof d.answer.sdp === "string" && d.answer.sdp.length < 12000 && typeof d.offer_id === "string") {
      var rec = P2P.offers[d.offer_id];
      if (!rec) return;
      delete P2P.offers[d.offer_id];
      rec.c.pid = typeof d.peer_id === "string" ? d.peer_id : "";
      try {
        await rec.c.pc.setRemoteDescription({ type: "answer", sdp: d.answer.sdp });
      } catch (e) {
        try {
          rec.c.pc.close();
        } catch (x) {}
      }
    }
  }
  function p2pOpenTracker(url) {
    var s = { url: url, ws: null, tries: 0 };
    function open() {
      if (P2P.status === "off") return;
      try {
        s.ws = new WebSocket(url);
      } catch (e) {
        return;
      }
      s.ws.onopen = function () {
        s.tries = 0;
        if (P2P.status === "connecting") P2P.status = "on";
        p2pAnnounce();
        p2pPaint();
      };
      s.ws.onmessage = function (e) {
        if (typeof e.data !== "string" || e.data.length > 30000) return;
        var d;
        try {
          d = JSON.parse(e.data);
        } catch (x) {
          return;
        }
        p2pOnTracker(s, d);
      };
      s.ws.onclose = function () {
        if (P2P.status !== "off") setTimeout(open, Math.min(60000, 3000 * ++s.tries));
        p2pPaint();
      };
      s.ws.onerror = function () {};
    }
    s.reopen = open;
    P2P.socks.push(s);
    open();
  }
  function p2pStart() {
    if (P2P.status !== "off") return;
    if (typeof RTCPeerConnection === "undefined" || typeof WebSocket === "undefined") return toast("Этот браузер не поддерживает P2P");
    var cfg = p2pCfg();
    P2P.status = "connecting";
    P2P.byPid = {};
    P2P.pid = p2pRand(20);
    p2pSha1("perekup-mod-room:" + cfg.room).then(function (h) {
      P2P.hash = h;
      var urls = cfg.tracker ? [cfg.tracker] : P2P_TRACKERS;
      urls.forEach(p2pOpenTracker);
      P2P.timer = setInterval(p2pAnnounce, 40000);
      p2pPaint();
    });
    p2pPaint();
  }
  function p2pStop() {
    P2P.status = "off";
    clearInterval(P2P.timer);
    P2P.socks.forEach(function (s) {
      try {
        s.ws.close();
      } catch (e) {}
    });
    P2P.socks = [];
    P2P.conns.slice().forEach(p2pDrop);
    Object.keys(P2P.offers).forEach(function (k) {
      try {
        P2P.offers[k].c.pc.close();
      } catch (e) {}
    });
    P2P.offers = {};
    P2P.peers = {};
    p2pPaint();
  }
  function p2pCleanPhone(src) {
    if (!src || typeof src !== "object" || Array.isArray(src)) return null;
    var out = {},
      n = 0;
    Object.keys(src).forEach(function (k) {
      if (k === "__proto__" || k === "constructor" || k === "prototype" || k === "mp" || k === "mod" || ++n > 40) return;
      var v = src[k];
      if (typeof v === "number" && isFinite(v)) out[k] = Math.max(-1e10, Math.min(1e10, v));
      else if (typeof v === "string") out[k] = v.replace(/[\u0000-\u001f<>]/g, "").slice(0, 80);
      else if (typeof v === "boolean") out[k] = v;
    });
    var r = null;
    ze.forEach(function (x) {
      if (x[1] === out.model && x[0] === out.brand) r = x;
    });
    if (!r || typeof out.cond !== "number") return null;
    out.id = 0;
    out.paid = Math.min(Math.max(0, out.paid || 0), bt(out)); /* цена закупки не может быть выше рыночной: подарком не накрутить капитал */
    return out;
  }
  function p2pOnMsg(c, raw) {
    if (typeof raw !== "string" || raw.length > 9000 || !o) return;
    var now = Date.now();
    c.rate = c.rate.filter(function (t) {
      return now - t < 10000;
    });
    if (c.rate.push(now) > 40) return; /* не больше 40 сообщений за 10 с от одной связи */
    var m;
    try {
      m = JSON.parse(raw);
    } catch (e) {
      return;
    }
    if (!m || typeof m !== "object" || typeof m.mid !== "string" || m.mid.length > 16 || P2P.seen[m.mid]) return;
    P2P.seen[m.mid] = now;
    Object.keys(P2P.seen).forEach(function (k) {
      if (now - P2P.seen[k] > 120000) delete P2P.seen[k];
    });
    if (m.t === "prof" && m.p && typeof m.p === "object") {
      var id = p2pStr(m.p.id, 12);
      if (id && id !== P2P.id && Object.keys(P2P.peers).length < 60)
        P2P.peers[id] = { id: id, nick: p2pStr(m.p.nick, 16) || "Игрок", city: p2pStr(m.p.city, 24), worth: p2pNum(m.p.worth, 1e12), deals: p2pNum(m.p.deals, 1e7), at: now };
    } else if (m.t === "chat") {
      var text = p2pStr(m.text, 200);
      if (text) P2P.chat.push({ nick: p2pStr(m.nick, 16) || "Игрок", text: text, at: now });
      if (P2P.chat.length > 60) P2P.chat.shift();
    } else if (m.t === "gift" && p2pStr(m.to, 12) === P2P.id) {
      var gid = p2pStr(m.gid, 16),
        fid = p2pStr(m.from, 12),
        ack = function (ok) {
          p2pBroadcast({ t: "ack", to: fid, gid: gid, ok: ok });
        };
      if (!gid || P2P.done[gid]) return ack(true);
      var ph = p2pCleanPhone(m.ph);
      if (!ph || !p2pCfg().gifts || o.phones.length >= 4900 || P2P.gifts >= 100 || now - (P2P.giftAt[fid] || 0) < 3000) return ack(false);
      P2P.giftAt[fid] = now;
      P2P.gifts++;
      ph.id = o.nextId++;
      o.phones.push(ph);
      P2P.done[gid] = 1;
      q();
      try {
        mmRef();
      } catch (e) {}
      toast("Подарок от " + p2pStr(m.fnick, 16) + ": " + F(ph));
      P2P.chat.push({ nick: "Система", text: p2pStr(m.fnick, 16) + " подарил вам " + F(ph), at: now });
      ack(true);
    } else if (m.t === "ack" && p2pStr(m.to, 12) === P2P.id) {
      var gk = p2pStr(m.gid, 16),
        e2 = P2P.escrow[gk];
      if (e2) {
        delete P2P.escrow[gk];
        if (!m.ok) {
          o.phones.push(e2.ph);
          q();
          try {
            mmRef();
          } catch (e) {}
          toast("Подарок не принят, телефон возвращён");
        }
      }
    }
    if (m.t === "prof" || m.t === "chat" || m.t === "gift" || m.t === "ack") P2P.conns.forEach(function (x) { if (x !== c) p2pSend(x, m); });
    p2pPaint();
  }
  function p2pGift(peerId, phoneId) {
    var i = -1;
    o.phones.forEach(function (p, k) {
      if (p.id === phoneId) i = k;
    });
    if (i < 0 || !P2P.peers[peerId]) return false;
    var ph = o.phones.splice(i, 1)[0],
      gid = p2pMid();
    P2P.escrow[gid] = { ph: ph, at: Date.now() };
    q();
    try {
      mmRef();
    } catch (e) {}
    p2pBroadcast({ t: "gift", to: peerId, from: P2P.id, fnick: p2pStr(o.nick, 16), gid: gid, ph: ph });
    setTimeout(function () {
      if (P2P.escrow[gid]) {
        o.phones.push(P2P.escrow[gid].ph);
        delete P2P.escrow[gid];
        q();
        try {
          mmRef();
        } catch (e) {}
        toast("Нет ответа от игрока, телефон возвращён");
      }
    }, 30000);
    return true;
  }
  function p2pChat(text) {
    text = p2pStr(text, 200).trim();
    if (!text) return;
    P2P.chat.push({ nick: p2pStr(o.nick, 16) + " (вы)", text: text, at: Date.now() });
    p2pBroadcast({ t: "chat", from: P2P.id, nick: p2pStr(o.nick, 16), text: text });
    p2pPaint();
  }
  setInterval(function () {
    if (!o || !P2P.conns.length) return;
    p2pBroadcast({ t: "prof", p: p2pProfile() });
    var now = Date.now();
    Object.keys(P2P.peers).forEach(function (k) {
      if (now - P2P.peers[k].at > 45000) delete P2P.peers[k];
    });
    p2pPaint();
  }, 10000);
  function vP2p() {
    var c = p2pCfg(),
      on = P2P.status !== "off";
    return (
      '<div class="mm-card"><h4>Общая комната мод-игроков</h4>' +
      '<div class="mm-row"><button class="mm-btn' + (on ? " red" : "") + '" data-a="p2ptoggle">' + (on ? "Выключить" : "Войти в комнату") + "</button></div>" +
      '<div class="mm-row"><label>Комната</label><input class="mm-in" id="mm-p2p-room" type="text" maxlength="30" value="' + esc(c.room) + '"' + (on ? " disabled" : "") + '></div>' +
      '<div class="mm-row"><label>Свой трекер (необязательно)</label><input class="mm-in" id="mm-p2p-trk" type="text" maxlength="100" placeholder="wss://…" value="' + esc(c.tracker) + '"' + (on ? " disabled" : "") + '></div>' +
      '<div class="mm-row"><label><input type="checkbox" id="mm-p2p-gifts"' + (c.gifts ? " checked" : "") + '> Принимать подарки-телефоны</label></div>' +
      '<div class="mm-muted">Все, кто зашёл в комнату с тем же названием, видят ваш ник, город, капитал и число сделок, а также ваш IP-адрес (так устроено P2P). Чтобы играть только со своими, придумайте уникальное название комнаты.</div></div>' +
      '<div id="mm-p2p-live"></div>'
    );
  }
  function p2pPaint() {
    var box = $("mm-p2p-live");
    if (!box || cur !== "p2p") return;
    var on = P2P.status !== "off",
      tb = D.querySelector('[data-a=p2ptoggle]');
    if (tb) {
      tb.textContent = on ? "Выключить" : "Войти в комнату";
      tb.classList.toggle("red", on);
    }
    var h = '<div class="mm-card"><h4>Статус: ' + (P2P.status === "off" ? "выключено" : P2P.status === "connecting" ? "подключаюсь к трекерам…" : P2P.conns.length ? "в комнате" : "жду других игроков") + "</h4>";
    if (!on) return void (box.innerHTML = h + "</div>");
    var me = p2pProfile(),
      list = [me].concat(
        Object.keys(P2P.peers).map(function (k) {
          return P2P.peers[k];
        }),
      );
    list.sort(function (a, b) {
      return b.worth - a.worth;
    });
    var opts = (o.phones || [])
      .map(function (p) {
        return '<option value="' + p.id + '">' + esc(F(p)) + "</option>";
      })
      .join("");
    h += '<div class="mm-muted">Игроков: ' + list.length + ", прямых связей: " + P2P.conns.length + "</div>";
    list.forEach(function (p, i) {
      h +=
        '<div class="mm-ph"><div><b>' + (i + 1) + ". " + esc(p.nick) + (p.id === P2P.id ? " (вы)" : "") + '</b><span class="mm-muted">' + esc(p.city) + " · " + money(p.worth) + " · " + p.deals + " сд.</span></div>" +
        (p.id !== P2P.id && opts ? '<span><select class="mm-sel" id="mm-g-' + esc(p.id) + '">' + opts + '</select> <button class="mm-btn" data-a="p2pgift" data-id="' + esc(p.id) + '">Подарить</button></span>' : "") +
        "</div>";
    });
    h += "</div>";
    h += '<div class="mm-card"><h4>Чат</h4><div id="mm-chat" style="max-height:180px;overflow:auto;font-size:13px">';
    P2P.chat.slice(-30).forEach(function (c2) {
      h += "<div><b>" + esc(c2.nick) + ":</b> " + esc(c2.text) + "</div>";
    });
    h += '</div><div class="mm-row"><input class="mm-in" id="mm-chat-in" type="text" maxlength="200" placeholder="Сообщение"><button class="mm-btn" data-a="p2pchat">Отправить</button></div></div>';
    var keep = $("mm-chat-in") ? $("mm-chat-in").value : "",
      foc = D.activeElement && D.activeElement.id === "mm-chat-in";
    box.innerHTML = h;
    if (keep) $("mm-chat-in").value = keep;
    if (foc) $("mm-chat-in").focus();
    var ch = $("mm-chat");
    if (ch) ch.scrollTop = ch.scrollHeight;
  }

  function vList() {
    var m0 = subOptions(0);
    var mine = (o.listings || []).filter(function (l) {
      return l.mod;
    });
    var h =
      '<div class="mm-card"><h4>Выставить своё объявление в ленту</h4>' +
      '<div class="mm-row"><input class="mm-in" id="mm-lq" type="text" placeholder="Поиск модели…"></div>' +
      '<div class="mm-row"><label>Модель</label><select class="mm-sel" id="mm-lm" style="flex-basis:100%">' + modelOptions("") + "</select></div>" +
      '<div class="mm-row"><label>Память</label><select class="mm-sel" id="mm-lg">' + m0.gb + '</select><label>Цвет</label><select class="mm-sel" id="mm-lc">' + m0.col + "</select></div>" +
      '<div class="mm-row"><label>Состояние</label><select class="mm-sel" id="mm-lcond">' +
      ft.map(function (c, i) {
        return '<option value="' + i + '">' + esc(c[0]) + "</option>";
      }).join("") +
      '</select><label>Комплект</label><select class="mm-sel" id="mm-lkit">' +
      $s.map(function (k, i) {
        return '<option value="' + esc(k) + '"' + (i === $s.length - 1 ? " selected" : "") + ">" + esc(k) + "</option>";
      }).join("") +
      "</select></div>" +
      '<div class="mm-row"><label>Батарея, %</label><input class="mm-in" id="mm-lbat" type="number" min="1" max="100" value="90"><label>Цена в объявлении, ₽</label><input class="mm-in" id="mm-lprice" type="text" inputmode="numeric" placeholder="обязательно"></div>' +
      '<div class="mm-row"><label>Справедливая цена, ₽ (необязательно, по умолчанию = цена)</label><input class="mm-in" id="mm-lfair" type="text" inputmode="numeric" style="flex-basis:100%"></div>' +
      '<div class="mm-row"><label>Описание и дефекты</label><textarea class="mm-in" id="mm-ldesc" rows="4" maxlength="500" placeholder="Например: трещина на задней крышке, не работает кнопка громкости…" style="flex-basis:100%"></textarea></div>' +
      '<div class="mm-row"><label>Продавец</label><input class="mm-in" id="mm-lsel" type="text" maxlength="30" placeholder="Частник"><label>Рейтинг продавца</label><input class="mm-in" id="mm-lrate" type="number" min="1" max="5" step="0.1" value="4.8"></div>' +
      '<div class="mm-row"><label>Блокировка</label><select class="mm-sel" id="mm-llock"><option value="">Нет</option><option value="pass">Код-пароль (продаётся под восстановление)</option><option value="frp">Google-аккаунт (FRP)</option></select></div>' +
      '<div class="mm-row"><label>Своё название (необязательно)</label><input class="mm-in" id="mm-lname" type="text" maxlength="60" style="flex-basis:100%"></div>' +
      '<div class="mm-row"><button class="mm-btn" data-a="listput" style="flex:1">Выставить</button></div>' +
      '<div class="mm-muted">Объявление появляется вверху ленты «Перекуп» в вашем городе (' + esc(ti()) + "). Оно остаётся там при обновлении ленты, пока его не купят или не снимут.</div></div>";
    h += '<div class="mm-card"><h4>Мои объявления: ' + mine.length + "</h4>";
    if (!mine.length) h += '<div class="mm-muted">Пока нет.</div>';
    mine.forEach(function (l) {
      h += '<div class="mm-ph"><div><b>' + esc(l.cn || F(l)) + '</b><span class="mm-muted">' + money(l.price) + " · " + esc(l.city) + '</span></div><button class="mm-btn red" data-a="listdel" data-id="' + l.id + '">Снять</button></div>';
    });
    return h + "</div>";
  }

  var LOOK = { fab: 88, pan: 100 };
  var IMG = { fab: "", pan: "", game: "" };
  try {
    var lk = JSON.parse(localStorage.getItem("modmenu_look") || "null");
    if (lk) {
      if (typeof lk.fab === "number") LOOK.fab = Math.max(0, Math.min(100, lk.fab));
      if (typeof lk.pan === "number") LOOK.pan = Math.max(20, Math.min(100, lk.pan));
    }
    ["fab", "pan", "game"].forEach(function (k) {
      IMG[k] = localStorage.getItem("modmenu_img_" + k) || "";
    });
  } catch (e) {}
  function saveLook() {
    try {
      localStorage.setItem("modmenu_look", JSON.stringify(LOOK));
    } catch (e) {}
  }
  function applyLook() {
    fab.style.opacity = String(LOOK.fab / 100);
    panel.style.opacity = String(LOOK.pan / 100);
    fab.style.backgroundImage = IMG.fab ? "url(" + IMG.fab + ")" : "";
    fab.style.backgroundSize = "cover";
    fab.style.backgroundPosition = "center";
    fab.textContent = IMG.fab ? "" : "🛠";
    panel.style.backgroundImage = IMG.pan ? "url(" + IMG.pan + ")" : "";
    panel.style.backgroundSize = "cover";
    panel.style.backgroundPosition = "center";
    panel.classList.toggle("mm-img", !!IMG.pan);
    var gs = $("mm-gcss");
    if (!gs) {
      gs = D.createElement("style");
      gs.id = "mm-gcss";
      D.head.appendChild(gs);
    }
    gs.textContent = IMG.game
      ? "#screen-home{background-image:url(" + IMG.game + ") !important;background-size:cover !important;background-position:center !important}"
      : "";
  }
  function pickImage(kind, size) {
    var inp = D.createElement("input");
    inp.type = "file";
    inp.accept = "image/*";
    inp.onchange = function () {
      var f = inp.files && inp.files[0];
      if (!f) return;
      var rd = new FileReader();
      rd.onload = function () {
        var im = new Image();
        im.onload = function () {
          var w = im.width,
            h = im.height,
            c = D.createElement("canvas"),
            cx = c.getContext("2d");
          if (kind === "fab") {
            var m = Math.min(w, h);
            c.width = c.height = 160;
            cx.drawImage(im, (w - m) / 2, (h - m) / 2, m, m, 0, 0, 160, 160);
          } else {
            var sc = Math.min(1, size / Math.max(w, h));
            c.width = Math.max(1, Math.round(w * sc));
            c.height = Math.max(1, Math.round(h * sc));
            cx.drawImage(im, 0, 0, c.width, c.height);
          }
          var url = c.toDataURL("image/jpeg", 0.82);
          IMG[kind] = url;
          try {
            localStorage.setItem("modmenu_img_" + kind, url);
          } catch (e) {
            toast("Фото применено, но не сохранилось: мало места");
          }
          applyLook();
          if (cur === "look") render();
        };
        im.onerror = function () {
          toast("Не удалось открыть картинку");
        };
        im.src = rd.result;
      };
      rd.readAsDataURL(f);
    };
    inp.click();
  }
  function vLook() {
    function imgRow(kind, title, hint) {
      return (
        '<div class="mm-card"><h4>' + title + '</h4><div class="mm-row"><div class="mm-pv" style="' +
        (IMG[kind] ? "background-image:url(" + IMG[kind] + ")" : "") +
        '"></div><button class="mm-btn" data-a="img" data-k="' + kind + '">Выбрать фото</button>' +
        (IMG[kind] ? '<button class="mm-btn alt" data-a="imgdel" data-k="' + kind + '">Убрать</button>' : "") +
        '</div><div class="mm-muted">' + hint + "</div></div>"
      );
    }
    return (
      '<div class="mm-card"><h4>Прозрачность кнопки 🛠: <span id="mm-fabv">' + LOOK.fab + '%</span></h4><input type="range" min="0" max="100" step="1" id="mm-fabop" style="width:100%" value="' + LOOK.fab + '">' +
      '<div class="mm-muted">При 0% кнопка невидима, но остаётся на месте и нажимается. На компьютере меню открывает клавиша ` (Ё).</div></div>' +
      '<div class="mm-card"><h4>Прозрачность меню: <span id="mm-panv">' + LOOK.pan + '%</span></h4><input type="range" min="20" max="100" step="1" id="mm-panop" style="width:100%" value="' + LOOK.pan + '">' +
      '<div class="mm-muted">Не ниже 20%, чтобы меню нельзя было потерять.</div></div>' +
      imgRow("fab", "Иконка кнопки меню", "Картинка обрезается до квадрата и заменяет значок 🛠.") +
      imgRow("pan", "Фон мод-меню", "Карточки остаются читаемыми: под ними лёгкая подложка.") +
      imgRow("game", "Фон телефона (главный экран игры)", "Заменяет обои на главном экране игры. Применяется сразу.") +
      '<div class="mm-card"><div class="mm-row"><button class="mm-btn alt" data-a="lookreset">Сбросить вид</button></div></div>'
    );
  }

  function vNums() {
    var st = Qr(),
      tiers = mmNumTiers();
    var h =
      '<div class="mm-card"><h4>Номера на счету: <span id="mm-ncount">' +
      st.list.length +
      '</span></h4><div class="mm-muted">Прокрутки: ' +
      (st.spins | 0) +
      " · потрачено " +
      money(st.spent || 0) +
      "</div></div>" +
      '<div class="mm-card"><h4>Выдать номера</h4>' +
      '<div class="mm-row"><label>Свой номер (10 цифр после +7; пусто = случайные)</label><input class="mm-in" id="mm-nd" type="text" inputmode="tel" placeholder="9XX XXX-XX-XX" style="flex-basis:100%"></div>' +
      '<div class="mm-row"><label>Редкость</label><select class="mm-sel" id="mm-nt"><option value="-1">Авто (по цифрам / как в игре)</option>' +
      tiers
        .map(function (t, i) {
          return '<option value="' + i + '">' + esc(t.name) + " — " + money(t.v) + "</option>";
        })
        .join("") +
      "</select></div>" +
      '<div class="mm-row"><label>Количество (для случайных)</label><input class="mm-in" id="mm-nc" type="number" min="1" max="100" value="1"><label>Своя цена, ₽ (необязательно)</label><input class="mm-in" id="mm-nv" type="text" inputmode="numeric" placeholder="по редкости"></div>' +
      '<div class="mm-row"><button class="mm-btn" data-a="numgive" style="flex:1">Выдать</button></div>' +
      '<div class="mm-muted">Если указан свой номер и редкость «Авто», редкость считается по цифрам так же, как в игре (серии одинаковых цифр, зеркальные).</div></div>' +
      '<div class="mm-card"><div class="mm-row"><button class="mm-btn red" data-a="numclear">Удалить все номера</button></div></div>';
    return h;
  }
  function vServ() {
    return (
      '<div class="mm-card"><h4>Рынок и телефоны</h4><div class="mm-row">' +
      '<button class="mm-btn" data-a="svc_market">Обновить объявления</button>' +
      '<button class="mm-btn" data-a="svc_rare">Выдать случайный раритет</button>' +
      '<button class="mm-btn" data-a="svc_fix">Починить все телефоны</button></div>' +
      '<div class="mm-muted">«Починить»: состояние «как новый», батарея 100%, снимает блокировки, дефекты и проблемы с ОС у телефонов на складе.</div></div>' +
      '<div class="mm-card"><h4>Задания</h4><div class="mm-row">' +
      '<button class="mm-btn" data-a="svc_claim">Выполнить и забрать все задания дня</button>' +
      '<button class="mm-btn alt" data-a="svc_daily">Сгенерировать новые на сегодня</button></div></div>'
    );
  }

  /* ===== ДОПОЛНЕНИЕ: таймеры, аукцион, онлайн (без авторизации) ===== */
  var TM_MSG = "";
  function mmBmReset() {
    var b = Rt();
    b.nextPost = 0; b.postsKey = ""; b.night = { key: "", buys: 0 };
    try { Qs(); } catch (e) {}
    q();
    return "Тёмный склад обновлён";
  }
  function mmAucReset() {
    Oa = 0; ls = y();
    if (Ft && Ft.done) { Ft = null; }
    try { if (!Ft) cs(); ei(); } catch (e) {}
    q();
    return "Аукцион без ожидания";
  }
  function mmAucAdd(mi, gi, ci, bid, noBots, cond) {
    var arr = mmGive({ mi: mi, gi: gi, ci: ci, count: 1, cond: cond | 0, bat: 100, kit: $s[$s.length - 1], price: 0, paid: 0 });
    var u = arr[0]; if (!u) return "Лот не вышел";
    o.phones.splice(o.phones.indexOf(u), 1);
    if (Ft && !Ft.done && Ft.listing && Ft.item) Ft.item.auctionBusy = false;
    var mk = bt(u);
    Ft = { item: u, listing: false, endAt: y() + 12e4, market: mk,
      bid: bid > 0 ? Math.round(bid) : Math.max(500, Math.round(mk * .22 / 100) * 100),
      leader: "", leaderName: "Стартовая цена", botMax: noBots ? 0 : Math.round(mk * (.55 + K() * .3)),
      nextBotAt: y() + 5e3, bots: noBots ? 0 : 2, done: false };
    na = ""; Oa = 0;
    try { ei(); } catch (e) {}
    q();
    return "На аукционе: " + F(u);
  }
  /* ===== ещё: юани, TON, выигрыш ===== */
  function mmYuan(v, set) { var c = chnState(); c.cny = Math.max(0, Math.round(set ? v : (c.cny || 0) + v)); q(); return c.cny; }
  function mmTon(v) { var h = cxS().hold; h.TON = Math.max(0, (h.TON || 0) + v); q(); return h.TON; }
  function vTm() {
    var cn = 0; try { cn = chnState().cny || 0; } catch (x) {}
    return '<div class="mm-card"><h4>Казино</h4><div class="mm-row"><button class="mm-btn' + (mmWin() ? "" : " alt") + '" data-a="x_win">Всегда выигрыш: ' + (mmWin() ? "вкл" : "выкл") + '</button></div></div>' +
      '<div class="mm-card"><h4>Юани ¥: <span id="mm-cny">' + Math.round(cn).toLocaleString("ru-RU") + '</span></h4><div class="mm-row">' +
      [1e3, 1e5, 1e7, 1e9].map(function (v) { return '<button class="mm-btn" data-a="x_yuan" data-v="' + v + '">+' + v.toLocaleString("ru-RU") + "</button>"; }).join("") + '</div>' +
      '<div class="mm-row"><input class="mm-in" id="mm-yv" type="text" inputmode="numeric" placeholder="Своё число"><button class="mm-btn alt" data-a="x_yset">Поставить</button></div></div>' +
      '<div class="mm-card"><h4>TON</h4><div class="mm-row">' +
      [100, 1e4, 1e6].map(function (v) { return '<button class="mm-btn" data-a="x_ton" data-v="' + v + '">+' + v.toLocaleString("ru-RU") + "</button>"; }).join("") + '</div></div>' +
      '<div class="mm-card"><h4>Тёмный склад</h4><div class="mm-row"><button class="mm-btn" data-a="x_bm">Сбросить таймер</button></div></div>' +
      '<div class="mm-card"><h4>Аукцион</h4><div class="mm-row"><button class="mm-btn" data-a="x_aucreset">Сбросить время ожидания</button></div>' +
      '<div class="mm-row"><label>Свой лот</label><select class="mm-sel" id="mm-am" style="flex-basis:100%">' + modelOptions("") + '</select></div>' +
      '<div class="mm-row"><select class="mm-sel" id="mm-ac">' + ft.map(function (c, i) { return '<option value="' + i + '">' + esc(c[0]) + "</option>"; }).join("") + '</select></div>' +
      '<div class="mm-row"><input class="mm-in" id="mm-abid" type="text" inputmode="numeric" placeholder="Стартовая ставка"></div>' +
      '<div class="mm-row"><label><input type="checkbox" id="mm-anob"> без ботов</label></div>' +
      '<div class="mm-row"><button class="mm-btn alt" data-a="x_aucadd">Выставить</button></div></div>' +
      '';
  }
  /* --- онлайн: адрес и ключ зашиты в сборку (window.PK_SERVER) --- */
  var ON = { ws: null, id: 0, users: [], chat: [], st: "ищу сервер" };
  function onCfg() {
    return null;
    var c = window.PK_SERVER || {}, u = c.url || "";
    if (!u || u.indexOf("__") === 0) {
      if (/^https?:$/.test(location.protocol)) u = (location.protocol === "https:" ? "wss://" : "ws://") + location.host;
      else return null;
    }
    return { url: u, key: c.key && c.key.indexOf("__") !== 0 ? c.key : "perekup-mod" };
  }
  function onConnect() {
    var c = onCfg(); if (!c || !o) return;
    try { ON.ws = new WebSocket(c.url); } catch (e) { ON.ws = null; return; }
    var w = ON.ws;
    w.onopen = function () { w.send(JSON.stringify({ t: "hello", key: c.key, name: String(o.nick || "").slice(0, 24) })); onScore(); };
    w.onmessage = function (ev) {
      var m; try { m = JSON.parse(ev.data); } catch (e) { return; }
      if (m.t === "welcome") { ON.id = m.id; ON.st = "онлайн"; }
      else if (m.t === "board") window.__modBoard = (m.rows || []).filter(function (r) { return r && r.cid !== onCid(); }).slice(0, 500);
      else if (m.t === "roster") ON.users = (m.users || []).slice(0, 200);
      else if (m.t === "chat") { ON.chat.push({ n: String(m.from).slice(0, 24), t: String(m.text).slice(0, 200) }); if (ON.chat.length > 60) ON.chat.shift(); }
      else if (m.t === "gift" && o) {
        var ph = null; try { ph = p2pCleanPhone(m.item); } catch (x) {}
        if (ph) {
          ph.id = o.nextId++; ph.paid = Math.min(ph.paid || 0, bt(ph)); delete ph.auctionBusy;
          o.phones.push(ph); q(); toast("🎁 Подарок от " + String(m.from).slice(0, 24) + ": " + F(ph));
        }
      }
      onPaint();
    };
    w.onclose = function () { if (ON.ws === w) ON.ws = null; ON.users = []; ON.st = "нет связи"; onPaint(); };
    w.onerror = function () { try { w.close(); } catch (e) {} };
  }
  function onCid() {
    try { var c = localStorage.getItem("modmenu_cid"); if (!c) { c = Math.random().toString(36).slice(2, 12) + Date.now().toString(36); localStorage.setItem("modmenu_cid", c); } return c; } catch (x) { return "anon" + String(o && o.nick).length + "xxxxxxxx"; }
  }
  function onScore() {
    if (!o || !ON.ws || ON.ws.readyState !== 1) return;
    var w = o.balance || 0; (o.phones || []).forEach(function (p) { w += p.paid || 0; });
    ON.ws.send(JSON.stringify({ t: "score", cid: onCid(), nick: o.nick, city: o.city, worth: Math.round(w), deals: o.deals | 0 }));
  }
  setInterval(onScore, 20000);
  setInterval(function () { if (o && !ON.ws) onConnect(); }, 6000);
  function onPaint() { if (cur === "player" && o && D.getElementById("mm-panel").classList.contains("on")) { var sc = bodyEl.scrollTop; render(); bodyEl.scrollTop = sc; } }
  function vOnl() {
    if (!onCfg()) return "";
    var on = ON.ws && ON.ws.readyState === 1;
    var ph = o.phones.map(function (p) { return '<option value="' + p.id + '">' + esc(F(p)) + "</option>"; }).join("");
    var us = ON.users.filter(function (u) { return u.id !== ON.id; }).map(function (u) { return '<option value="' + u.id + '">' + esc(u.name) + "</option>"; }).join("");
    return '<div class="mm-card"><h4>Игроки · ' + esc(ON.st) + (on ? " (" + ON.users.length + ")" : "") + "</h4>" + (on ?
      '<div class="mm-row"><select class="mm-sel" id="mm-ot" style="flex-basis:100%">' + (us || "<option value=''>пока никого</option>") + "</select></div>" +
      '<div class="mm-row"><select class="mm-sel" id="mm-op" style="flex-basis:100%">' + (ph || "<option value=''>нет телефонов</option>") + "</select></div>" +
      '<div class="mm-row"><button class="mm-btn alt" data-a="x_ogift">Подарить телефон</button></div>' +
      '<div style="max-height:140px;overflow:auto;font-size:13px">' + ON.chat.map(function (c) { return "<div><b>" + esc(c.n) + ":</b> " + esc(c.t) + "</div>"; }).join("") + "</div>" +
      '<div class="mm-row"><input class="mm-in" id="mm-oc" placeholder="Сообщение"><button class="mm-btn" data-a="x_ochat">→</button></div>' : "") + "</div>";
  }
  function mmExtra(a, b) {
    var m = null;
    if (a === "x_bm") m = mmBmReset();
    else if (a === "x_win") { try { localStorage.setItem("modmenu_win", mmWin() ? "0" : "1"); } catch (x) {} m = mmWin() ? "Выигрыш включён" : "Выигрыш выключен"; }
    else if (a === "x_yuan") m = "Юаней: " + Math.round(mmYuan(+b.dataset.v)).toLocaleString("ru-RU");
    else if (a === "x_yset") { var yv = num($("mm-yv").value, NaN); if (isNaN(yv)) { toast("Введи число"); return true; } m = "Юаней: " + Math.round(mmYuan(yv, true)).toLocaleString("ru-RU"); }
    else if (a === "x_ton") { try { m = "TON: " + mmTon(+b.dataset.v).toLocaleString("ru-RU"); } catch (x) { m = "Биржа ещё не открыта"; } }
    else if (a === "x_aucreset") m = mmAucReset();
    else if (a === "x_aucnew") { Ft = null; Oa = 0; try { cs(); ei(); } catch (x) {} q(); m = "Новый лот"; }
    else if (a === "x_aucadd") m = mmAucAdd(+$("mm-am").value || 0, 0, 0, num($("mm-abid").value, 0), $("mm-anob").checked, +$("mm-ac").value);
    else if (a === "x_ochat") {
      var t = $("mm-oc").value.trim(); if (t && ON.ws && ON.ws.readyState === 1) ON.ws.send(JSON.stringify({ t: "chat", text: t })); $("mm-oc").value = ""; return true;
    } else if (a === "x_ogift") {
      var to = +$("mm-ot").value, pid = +$("mm-op").value, i = -1;
      o.phones.forEach(function (p, k) { if (p.id === pid) i = k; });
      if (!to || i < 0 || !ON.ws || ON.ws.readyState !== 1) { toast("Выбери игрока и телефон"); return true; }
      var item = o.phones.splice(i, 1)[0]; ON.ws.send(JSON.stringify({ t: "gift", to: to, item: item })); q();
      toast("🎁 Ушло"); render(); return true;
    } else return false;
    TM_MSG = m; toast(m); render(); return true;
  }

  function render() {
    drawTabs();
    if (!o) {
      bodyEl.innerHTML = '<div class="mm-keep" style="padding:14px 0">Сначала создай персонажа</div>';
      return;
    }
    mC();
    bodyEl.innerHTML = { player: function () { return vPlayer() + vOnl(); }, casino: vCasino, give: vGive, stock: vStock, nums: vNums, list: vList, tm: vTm, cars: vCars, look: vLook, serv: vServ, data: vData }[cur]() + '<div class="mm-wm">by TG Sync_sm</div><div class="mm-zay">За двумя зайцами - воробей с яйцами. @ki_l1</div>';
    if (cur === "p2p") p2pPaint();
  }

  function st0() {
    return Qr();
  }
  function refreshBal() {
    var b = $("mm-bal");
    if (b) b.textContent = money(o.balance);
  }

  bodyEl.addEventListener("click", function (e) {
    var b = e.target.closest("[data-a]");
    if (!b || !o) return;
    var a = b.dataset.a;
    if (mmExtra(a, b)) return;
    if (a === "cash") {
      var gv = mmCasino(+b.dataset.v);
      toast(gv ? "Казино: " + (gv > 0 ? "+" : "") + money(gv) : "Достигнут лимит " + MM_MAX_MONEY.toLocaleString("ru-RU") + " ₽");
      refreshBal();
    } else if (a === "cashcustom") {
      var v = num($("mm-amt").value, NaN);
      if (isNaN(v) || !v) return toast("Введите сумму");
      var gc = mmCasino(v);
      toast(gc ? "Казино: " + (gc > 0 ? "+" : "") + money(gc) : "Ничего не изменилось: лимит " + MM_MAX_MONEY.toLocaleString("ru-RU") + " ₽");
      refreshBal();
    } else if (a === "cashset") {
      var tv = num($("mm-amt").value, NaN);
      if (isNaN(tv) || tv < 0) return toast("Введите нужный баланс");
      tv = Math.min(MM_MAX_MONEY, Math.round(tv));
      var gs = mmCasino(tv - Math.round(o.balance));
      toast("Баланс: " + money(o.balance));
      refreshBal();
    } else if (a === "taxclear") {
      toast("Налоговых дней закрыто: " + mmTaxClear());
    } else if (a === "unblock") {
      o.support = o.support || { tickets: [], blocked: [] };
      o.support.blocked = [];
      q();
      toast("Блокировки сняты");
    } else if (a === "repmax") {
      o.rep = 100;
      q();
      try {
        Fn();
      } catch (x) {}
      render();
    } else if (a === "upgmax") {
      o.upg = o.upg || { d: 0, r: 0, b: 0, s: 0 };
      Ul.forEach(function (u) {
        o.upg[u.id] = 5;
      });
      q();
      mmRef();
      render();
    } else if (a === "pump") {
      var hw = hwState();
      if (hw) {
        hw.p = Math.min(MM_MAX_PUMP, hw.p + +b.dataset.v);
        q();
        try {
          hwRender();
          hwDot();
        } catch (x) {}
        $("mm-pump").textContent = hw.p;
      }
    } else if (a === "loan0") {
      o.loan = null;
      q();
      mmRef();
      toast("Кредит списан");
      render();
    } else if (a === "repfin") {
      var n = 0;
      (o.repairs || []).forEach(function (r) {
        r.doneAt = y();
        n++;
      });
      q();
      toast("Ремонтов завершено: " + n);
    } else if (a === "tripfin") {
      if (o.travel) {
        o.travel.end = y();
        q();
        toast("Поездка завершается");
      } else toast("Вы сейчас не в пути");
    } else if (a === "opencasino") {
      toggle(false);
      try {
        Ln();
        I("casino");
      } catch (x) {}
    } else if (a === "give") {
      var mi = +$("mm-m").value;
      if (isNaN(mi) || !ze[mi]) return toast("Выберите модель");
      var cnt = Math.max(1, Math.min(50, Math.round(num($("mm-cnt").value, 1))));
      var price = num($("mm-price").value, 0),
        paidv = $("mm-paid").value.trim() === "" ? null : Math.max(0, num($("mm-paid").value, 0));
      var res = mmGive({
        mi: mi,
        gi: +$("mm-g").value,
        ci: +$("mm-c").value,
        cond: +$("mm-cond").value,
        bat: Math.max(1, Math.min(100, Math.round(num($("mm-bat").value, 100)))),
        kit: $("mm-kit").value,
        count: cnt,
        name: $("mm-name").value.trim(),
        price: price > 0 ? price : 0,
        paid: paidv,
      });
      toast("Выдано: " + res.length + " шт. · склад " + o.phones.length);
    } else if (a === "edit") {
      editId = editId === +b.dataset.id ? null : +b.dataset.id;
      render();
    } else if (a === "esave") {
      var p = o.phones.filter(function (x) {
        return x.id === +b.dataset.id;
      })[0];
      if (!p) return;
      var nm = $("mm-en").value.trim(),
        pr = num($("mm-ep").value, 0);
      if (nm) p.cn = nm;
      else delete p.cn;
      if (pr > 0) p.mp = Math.round(pr);
      else delete p.mp;
      p.cond = +$("mm-ec").value;
      p.bat = Math.max(1, Math.min(100, Math.round(num($("mm-eb").value, p.bat || 100))));
      q();
      mmRef();
      toast("Сохранено");
      render();
    } else if (a === "edel") {
      var id = +b.dataset.id;
      if (!confirm("Удалить телефон навсегда?")) return;
      o.phones = o.phones.filter(function (x) {
        return x.id !== id;
      });
      editId = null;
      q();
      mmRef();
      render();
    } else if (a === "export") {
      $("mm-exp").value = JSON.stringify(o);
      $("mm-exp").focus();
      $("mm-exp").select();
    } else if (a === "import") {
      var txt = $("mm-imp").value.trim(),
        obj;
      try {
        obj = JSON.parse(txt);
      } catch (x) {
        return toast("Это не JSON");
      }
      if (!obj || typeof obj.nick != "string" || typeof obj.balance != "number" || !Array.isArray(obj.phones)) return toast("Не похоже на сохранение игры");
      if (!confirm("Заменить текущее сохранение?")) return;
      if (Ce(xe, _t.enc(JSON.stringify(obj)))) location.reload();
      else toast("Не удалось записать");
    } else if (a === "numgive") {
      var raw = $("mm-nd").value.replace(/\D/g, ""),
        digits = "";
      if (raw) {
        if (raw.length === 11 && (raw[0] === "7" || raw[0] === "8")) raw = raw.slice(1);
        if (raw.length !== 10) return toast("Номер: 10 цифр после +7");
        digits = raw;
      }
      var nti = +$("mm-nt").value,
        tier = nti >= 0 ? mmNumTiers()[nti] : null,
        nprice = num($("mm-nv").value, 0),
        ncnt = Math.max(1, Math.min(100, Math.round(num($("mm-nc").value, 1))));
      if (st0().list.length + ncnt > 1000) return toast("В списке не больше 1000 номеров");
      var nres = mmNumGive({ digits: digits, tier: tier, price: nprice > 0 ? nprice : 0, count: ncnt });
      toast("Номеров выдано: " + nres.length + (nres.length === 1 ? " · " + nres[0].n + " · " + nres[0].r : ""));
      $("mm-ncount").textContent = st0().list.length;
    } else if (a === "numclear") {
      if (!confirm("Удалить все номера?")) return;
      st0().list = [];
      q();
      try {
        rs();
      } catch (x) {}
      render();
    } else if (a === "svc_market") {
      hn(true);
      mmRef();
      toast("Объявления обновлены");
    } else if (a === "svc_rare") {
      var ri = ze.indexOf(ji[Math.floor(Math.random() * ji.length)]);
      if (ri < 0) return toast("Раритетов в каталоге нет");
      var rr = mmGive({ mi: ri, gi: 0, ci: 0, cond: 0, bat: 100, kit: $s[$s.length - 1], count: 1, name: "", price: 0, paid: null });
      toast("Выдан раритет: " + (rr[0] ? F(rr[0]) : ""));
    } else if (a === "svc_fix") {
      var fx = 0;
      o.phones.forEach(function (p) {
        if (p.hwk) return;
        p.cond = 0;
        p.bat = 100;
        delete p.lock;
        delete p.flt;
        delete p.osBad;
        delete p.dead;
        fx++;
      });
      q();
      mmRef();
      toast("Починено: " + fx);
    } else if (a === "svc_claim") {
      var ac = Ei();
      On();
      var n2 = 0;
      ac.daily.tasks.forEach(function (t, i) {
        if (t.claimed) return;
        t.progress = t.goal;
        t.done = !0;
        gu(i);
        n2++;
      });
      toast(n2 ? "Заданий выполнено: " + n2 : "Все задания дня уже забраны");
      refreshBal();
    } else if (a === "svc_daily") {
      var ad = Ei();
      try {
        delete (ad.dHist || {})[Qe()];
      } catch (x) {}
      ad.daily = null;
      On();
      aa();
      ia();
      q();
      toast("Новые задания на сегодня");
    } else if (a === "p2ptoggle") {
      if (P2P.status === "off") {
        var pc0 = p2pCfg(),
          rm = $("mm-p2p-room").value.trim().slice(0, 30) || "all",
          tk = $("mm-p2p-trk").value.trim();
        if (tk && !/^(wss:\/\/[A-Za-z0-9.:\/_\-]+|ws:\/\/(localhost|127\.0\.0\.1)(:\d+)?\/?)$/.test(tk)) return toast("Адрес трекера должен начинаться с wss://");
        pc0.room = rm;
        pc0.tracker = tk;
        mmSave();
        p2pStart();
      } else p2pStop();
      render();
    } else if (a === "p2pchat") {
      var ci = $("mm-chat-in");
      p2pChat(ci.value);
      ci.value = "";
    } else if (a === "p2pgift") {
      var sel = $("mm-g-" + b.dataset.id);
      if (sel && p2pGift(b.dataset.id, +sel.value)) toast("Отправлено, жду подтверждения");
    } else if (a === "cargive") {
      var cm = $("mm-cm").value,
        cy = num($("mm-cy").value, 0);
      var cr2 = mmGiveCar({ m: cm, y: cy > 0 ? cy : 0, km: num($("mm-ckm").value, 0), col: +$("mm-cc").value, cond: +$("mm-cq").value });
      toast(cr2 ? "В гараже: " + arName(cr2) : "Не получилось");
      render();
    } else if (a === "carfix") {
      toast("Починено машин: " + mmCarsFix());
    } else if (a === "cardone") {
      toast("Работ завершено: " + mmCarsDone());
    } else if (a === "carmkt") {
      arRefresh(true);
      mmArRefresh();
      toast("Авторынок обновлён");
    } else if (a === "carbm") {
      arBmRefresh(true);
      mmArRefresh();
      toast("Чёрный рынок обновлён");
    } else if (a === "deals") {
      o.deals = Math.min(1e9, (o.deals | 0) + +b.dataset.v);
      q();
      $("mm-deals").textContent = o.deals.toLocaleString("ru-RU");
    } else if (a === "dealsset") {
      var dn = num($("mm-dn").value, NaN);
      if (isNaN(dn) || dn < 0) return toast("Введите число сделок");
      o.deals = Math.min(1e9, Math.round(dn));
      q();
      $("mm-deals").textContent = o.deals.toLocaleString("ru-RU");
    } else if (a === "move") {
      var cty = $("mm-city").value;
      if (cty === ti()) return toast("Вы уже в этом городе");
      mmMove(cty);
      toast("Вы переехали: " + cty);
      render();
    } else if (a === "listput") {
      var lmi = +$("mm-lm").value;
      if (isNaN(lmi) || !ze[lmi]) return toast("Выберите модель");
      var lp = num($("mm-lprice").value, 0);
      if (!(lp > 0)) return toast("Укажите цену");
      var ld = mmListing({
        mi: lmi,
        gi: +$("mm-lg").value,
        ci: +$("mm-lc").value,
        cond: +$("mm-lcond").value,
        bat: Math.max(1, Math.min(100, Math.round(num($("mm-lbat").value, 90)))),
        kit: $("mm-lkit").value,
        price: lp,
        fair: num($("mm-lfair").value, 0),
        desc: $("mm-ldesc").value.trim(),
        seller: $("mm-lsel").value.trim(),
        rating: Math.max(1, Math.min(5, num($("mm-lrate").value, 4.8))),
        lock: $("mm-llock").value,
        name: $("mm-lname").value.trim(),
      });
      toast(ld ? "Выставлено: " + F(ld) + " за " + money(ld.price) : "Не получилось");
      render();
    } else if (a === "listdel") {
      var lid = +b.dataset.id;
      o.listings = o.listings.filter(function (x) {
        return x.id !== lid;
      });
      q();
      try {
        te();
        Vt();
      } catch (x) {}
      mmRef();
      render();
    } else if (a === "img") {
      pickImage(b.dataset.k, b.dataset.k === "pan" ? 900 : 1100);
    } else if (a === "imgdel") {
      IMG[b.dataset.k] = "";
      try {
        localStorage.removeItem("modmenu_img_" + b.dataset.k);
      } catch (x) {}
      applyLook();
      render();
    } else if (a === "lookreset") {
      LOOK = { fab: 88, pan: 100 };
      IMG = { fab: "", pan: "", game: "" };
      try {
        localStorage.removeItem("modmenu_look");
        ["fab", "pan", "game"].forEach(function (k) {
          localStorage.removeItem("modmenu_img_" + k);
        });
      } catch (x) {}
      applyLook();
      render();
    } else if (a === "resetmod") {
      MM.casino = { mode: "off", boost: 30, pay: 1 };
      mmSave();
      toast("Настройки сброшены");
      render();
    }
  });

  bodyEl.addEventListener("input", function (e) {
    var t = e.target;
    if (t.id === "mm-rep" && o) {
      o.rep = +t.value;
      $("mm-repv").textContent = t.value;
    } else if (t.id === "mm-boost") {
      MM.casino.boost = +t.value;
      $("mm-bv").textContent = t.value + "%";
    } else if (t.id === "mm-cr" && o) {
      $("mm-crv").textContent = t.value;
    } else if (t.id === "mm-gar" && mmHasCars()) {
      AR_CFG.garage = +t.value;
      MM.garage = +t.value;
      mmSave();
      mmArRefresh();
    } else if (t.id === "mm-p2p-gifts") {
      p2pCfg().gifts = t.checked;
      mmSave();
    } else if (t.id === "mm-fabop") {
      LOOK.fab = +t.value;
      $("mm-fabv").textContent = t.value + "%";
      applyLook();
    } else if (t.id === "mm-panop") {
      LOOK.pan = +t.value;
      $("mm-panv").textContent = t.value + "%";
      applyLook();
    } else if (t.id === "mm-lq") {
      $("mm-lm").innerHTML = modelOptions(t.value);
      syncSubL();
    } else if (t.id === "mm-q") {
      var sel = $("mm-m");
      sel.innerHTML = modelOptions(t.value);
      syncSub();
    }
  });
  bodyEl.addEventListener("change", function (e) {
    var t = e.target;
    if (t.id === "mm-cr" && o) {
      mmCredit(+t.value);
      render();
    } else if (t.id === "mm-fabop" || t.id === "mm-panop") saveLook();
    else if (t.id === "mm-lm") syncSubL();
    else if (t.id === "mm-rep" && o) q();
    else if (t.id === "mm-boost") mmSave();
    else if (t.id === "mm-pay") {
      MM.casino.pay = +t.value;
      mmSave();
    } else if (t.dataset && t.dataset.a === "cmode") {
      MM.casino.mode = t.value;
      mmSave();
    } else if (t.dataset && t.dataset.a === "upg" && o) {
      o.upg[t.dataset.k] = +t.value;
      q();
      mmRef();
    } else if (t.id === "mm-m") syncSub();
  });
  function syncSubL() {
    var sel = $("mm-lm");
    if (!sel) return;
    var o2 = subOptions(+sel.value || 0);
    $("mm-lg").innerHTML = o2.gb;
    $("mm-lc").innerHTML = o2.col;
  }
  function syncSub() {
    var sel = $("mm-m");
    if (!sel) return;
    var o2 = subOptions(+sel.value || 0);
    $("mm-g").innerHTML = o2.gb;
    $("mm-c").innerHTML = o2.col;
  }

  applyLook();
  window.__mod = {
    get state() {
      return o;
    },
    cfg: MM,
    give: mmGive,
    money: mmMoney,
    cash: mmCasino,
    move: mmMove,
    credit: mmCredit,
    listing: mmListing,
    numbers: mmNumGive,
    open: function () {
      toggle(true);
    },
  };
}
