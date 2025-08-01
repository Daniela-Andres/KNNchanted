package de

/**
 * Hält Metainformationen über die eingestellten Parameter des KNN-Algorithmus
 */
object AlgoInfo {

  var kWert = 3

  //Metrik 0 --> euklid, Metrik 1 --> manhatten
  var metrik = 0

  //Gewichtung 0 --> individuell, 1 --> quadratisch
  var gewichtung = 0

  //Nur bei Gewichtung 0
  var gewichtX = 1
  var gewichtY = 1

  //Nur bei Gewichtung 1
  var alpha = 1

}
