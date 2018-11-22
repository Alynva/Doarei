import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import { calculaDistancia } from "./calculaDistancia"

admin.initializeApp(functions.config().firebase);

const db = admin.firestore();
db.settings({timestampsInSnapshots: true});

export const get_nears = functions.https.onRequest((request, response) => {
	const resposta = {erro: null, data: null},
		{ lat, long } = request.body.data,
		marco_0 = { lat: lat, long: long }
	db.collection("users").get()
		.then(snap => {
			const pertos = []
			snap.forEach(doc => {
				const doc_data = doc.data().adress.split(", "),
					localizacao = { lat: Number(doc_data[0]), long: Number(doc_data[1])},
					distancia = calculaDistancia(marco_0, localizacao)

					if (distancia <= 5*1e3) { // < 5km
						const person_info = doc.data(),
							info_filtered = {
								nome: person_info.nome,
								email: person_info.email,
								tipo: person_info.tipo,
								adress: person_info.adress
							}
						
						pertos.push(info_filtered)
					}
			})
			resposta.data = JSON.stringify({pertos})
			response.send(resposta)
		}).catch(e => {
			console.log("Erro ao consultar usuários.", e)
			resposta.erro = e
			response.send(resposta)
		})
});