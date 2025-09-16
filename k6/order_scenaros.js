import http from 'k6/http';
import { check, sleep } from 'k6';

const env = JSON.parse(open('./env.json'));
const BASE = env.baseUrl || 'http://localhost:8888';
const USERS = env.orderUserIds;
const ITEMS = env.itemIds;

function pickUser() {
    return USERS[Math.floor(Math.random() * USERS.length)];
}

function pickItem() {
    return ITEMS[Math.floor(Math.random() * ITEMS.length)];
}

export const options = {
    thresholds: {
        http_req_failed: ['rate<0.015'],
        'http_req_duration{api:order}': ['p(99)<1200'],
    },
    scenarios: {
        order_baseline: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 200,
            stages: [
                { target: 10, duration: '30s' },
                { target: 20, duration: '30s' },
                { target: 30, duration: '30s' },
            ],
            exec: 'orderScenario',
            tags: { api: 'order' }
        }
    }
};

export function orderScenario() {
    const userId = pickUser();
    const itemId = pickItem();
    const quantity = 1 + Math.floor(Math.random() * 2);

    const payload = JSON.stringify({
        userId,
        items: [{ itemId, quantity }]
    });

    const res = http.post(
        `${BASE}/api/v1/orders`,
        payload,
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(res, { 'order 2xx': (r) => r.status >= 200 && r.status < 300 });
    sleep(0.05);
}