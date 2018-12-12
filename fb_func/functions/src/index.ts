import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import { calculaDistancia, calculaAngulo } from "./calculos"

admin.initializeApp(functions.config().firebase);


const db = admin.firestore();
db.settings({timestampsInSnapshots: true});

export const get_nears = functions.https.onRequest((request, response) => {
	const resposta = {erro: null, data: null},
		{ lat, long } = request.body.data,
		marco_0 = { lat: lat, long: long }
		
	db.collection("users").get()
		.then(snap => {
			let pertos = []
			snap.forEach(doc => {
				const doc_data = doc.data().adress.split(", "),
					localizacao = { lat: Number(doc_data[0]), long: Number(doc_data[1])},
					distancia = calculaDistancia(marco_0, localizacao)

					if (distancia <= 5*1e3) { // < 5km
						let distancia_convertida
						if (distancia > 5*1e3) {
							distancia_convertida = "Mto longe"
						} else if (distancia >= 1e3) {
							distancia_convertida = `~${(distancia/1e3).toFixed(2)}km`
						} else if (distancia >= 20) {
							distancia_convertida = `~${distancia.toFixed(0)}m`
						} else {
							distancia_convertida = `Por aqui`
						}

						const person_info = doc.data();
						
						const info_filtered = {
								...person_info,
								distancia_real: distancia,
								distancia: distancia_convertida,
								angulo: calculaAngulo(marco_0, localizacao)
							}
						
						pertos.push(info_filtered)
					}
			})
			pertos = pertos.sort((a, b) => a.distancia_real < b.distancia_real ? -1 : 1)
			resposta.data = JSON.stringify({pertos})
			console.log(resposta)
			response.send(resposta)
		}).catch(e => {
			console.log("Erro ao consultar usuários.", e)
			resposta.erro = e
			response.send(resposta)
		})
});