/**
 * Environment (dev par défaut)
 * - firebaseEnabled: mettre à true et remplir firebaseConfig pour activer Firebase Auth
 * - En mode démo (firebaseEnabled: false), l'auth utilise le backend directement
 */

export const environment = {
  production: false,
  firebaseEnabled: false,
  firebaseConfig: {
    apiKey: '',
    authDomain: '',
    projectId: '',
    storageBucket: '',
    messagingSenderId: '',
    appId: ''
    // Copier la config Firebase ici et mettre firebaseEnabled: true
  }
};
