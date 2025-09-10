import http from 'k6/http';
import { check, sleep } from 'k6';

const env = JSON.parse(open('./env.json'));
const BASE = env.baseUrl || 'http://localhost:8888';
const USERS = Array.isArray(env.chargeUserIds) && env.userIds.length ? env.userIds : [1];

function pickUser() {
    return USERS[Math.floor(Math.random() * USERS.length)];
}

export const options = (() => {
    switch (__ENV.MODE) {
        case 'charge':
            return {
                thresholds: {
                    http_req_failed: ['rate<0.01'],
                    'http_req_duration{api:charge}': ['p(99)<800'],
                },
                scenarios: {
                    charge_short: {
                        executor: 'constant-arrival-rate',
                        rate: 100, timeUnit: '1s', duration: '30s',
                        preAllocatedVUs: 50, maxVUs: 100,
                        exec: 'chargeScenario', tags: { api: 'charge' },
                    },
                },
            };
        case 'balance':
            return {
                thresholds: {
                    http_req_failed: ['rate<0.01'],
                    'http_req_duration{api:balance}': ['p(99)<300'],
                },
                scenarios: {
                    balance_short: {
                        executor: 'constant-arrival-rate',
                        rate: 100, timeUnit: '1s', duration: '30s',
                        preAllocatedVUs: 50, maxVUs: 100,
                        exec: 'balanceScenario', tags: { api: 'balance' },
                    },
                },
            };
        default:
            throw new Error("MODE 환경변수 필요: charge | balance");
    }
})();

export function chargeScenario() {
    const userId = pickUser();
    const amount = 1000 + Math.floor(Math.random() * 5000);

    const res = http.post(`${BASE}/api/v1/accounts/charge`,
        JSON.stringify({ userId, amount }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(res, { 'charge 2xx': (r) => r.status >= 200 && r.status < 300 });
    sleep(0.02);
}

export function balanceScenario() {
    const userId = pickUser();
    const res = http.get(`${BASE}/api/v1/accounts/${userId}`);
    check(res, { 'balance 2xx': (r) => r.status >= 200 && r.status < 300 });
    sleep(0.01);
}