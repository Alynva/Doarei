import { degrees2radians, radians2degrees } from "./conversoes"

export const calculaDistancia = (ponto_1, ponto_2) => {
	const R = 6371e3; // metres
	const φ1 = degrees2radians(ponto_1.lat);
	const φ2 = degrees2radians(ponto_2.lat);
	const Δφ = degrees2radians(ponto_2.lat - ponto_1.lat);
	const Δλ = degrees2radians(ponto_2.long - ponto_1.long);

	const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
			Math.cos(φ1) * Math.cos(φ2) *
			Math.sin(Δλ/2) * Math.sin(Δλ/2);
	const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

	const d = R * c; // metres

	return d;
}

export const calculaAngulo = (ponto_1, ponto_2) => {
	return radians2degrees(Math.atan2(ponto_2.lat - ponto_1.lat, ponto_2.long - ponto_1.long))
}
