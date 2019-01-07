import java.util.*;

public class KNN {

    //
    static double euclidianDistance(Instances instance, Instances instance2) {
        double valor[] = new double[12];
        double soma = 0;
        // Characteristic such as duration_ms is irrelevant for the classification.
        // so i disconsidered it in this algorithm
        valor[0] = Math.pow(instance.getAcousticness() - instance2.getAcousticness(), 2);
        valor[1] = Math.pow(instance.getDanceability() - instance2.getDanceability(), 2);
        valor[2] = Math.pow(instance.getEnergy() - instance2.getEnergy(), 2);
        valor[3] = Math.pow(instance.getInstrumentalness() - instance2.getInstrumentalness(), 2);
        valor[4] = Math.pow(instance.getKey() - instance2.getKey(), 2);
        valor[5] = Math.pow(instance.getLiveness() - instance2.getLiveness(), 2);
        valor[6] = Math.pow(instance.getLoudness() - instance2.getLoudness(), 2);
        valor[7] = Math.pow(instance.getSpeechiness() - instance2.getSpeechiness(), 2);
        valor[8] = Math.pow(instance.getMode() - instance2.getMode(), 2);
        valor[9] = Math.pow(instance.getTempo() - instance2.getTempo(), 2);
        valor[10] = Math.pow(instance.getTime_signature() - instance2.getTime_signature(), 2);
        valor[11] = Math.pow(instance.getValence() - instance2.getValence(), 2);


        for (int i = 0; i < valor.length; i++) {
            soma += valor[i];
        }

        double sqrt = Math.sqrt(soma);
        return sqrt;


    }


    // initially i thought about making this method a weightedKNN, but tests have showed better results using a normal knn.

    static double knnCalculator(int k, List<Instances> baseInstances, int[][] matrix, Instances... instance) {  // using varargs
        List<AuxClass> aux = new ArrayList<>();
        AuxClass compare = new AuxClass();
        int count;
        double auxRightness = 0.0;
        double vpA, fpA, vnA, fnA; // A -> hip hop
        double vpB, fpB, vnB, fnB; // B -> pop
        double vpC, fpC, vnC, fnC; // C -> dance

        int countPop, countRap, countDance;

        for (Instances x : instance) {
            countPop = 0;
            countRap = 0;
            countDance = 0;
            count = 0;
            for (Instances y : baseInstances) {
                if (!x.equals(y)) {
                    AuxClass obj = new AuxClass(y.getClassification(), (KNN.euclidianDistance(x, y)));
                    aux.add(obj);
                }

            }

            Collections.sort(aux, compare);

            for (AuxClass var : aux) {

                if (count == k) {
                    break;
                }
                if (var.getClassification().equals("hip-hop/rap")) {
                    countRap++;

                } else if (var.getClassification().equals("pop")) {
                    countPop++;

                } else {
                    countDance++;

                }
                count++;
            }
            if ((Math.max(countRap, countPop) == countRap && Math.max(countPop, countDance) == countPop)
                    || (Math.max(countRap, countDance) == countRap && Math.max(countDance, countPop) == countDance)) {
                if (x.getClassification().equals("hip-hop/rap")) {
                    matrix[0][0] = ++matrix[0][0];
                    auxRightness++;
                } else if (x.getClassification().equals("pop")) {
                    matrix[1][0] = ++matrix[1][0];

                } else {

                }
            } else if ((Math.max(countPop, countRap) == countPop && Math.max(countRap, countDance) == countRap)
                    || (Math.max(countPop, countDance) == countPop && Math.max(countDance, countRap) == countDance)) {
                if (x.getClassification().equals("pop")) {
                    matrix[1][1] = ++matrix[1][1];
                    auxRightness++;
                } else if (x.getClassification().equals("hip-hop/rap")) {
                    matrix[0][1] = ++matrix[0][1];

                } else {
                    matrix[2][1] = ++matrix[2][1];

                }
            } else {
                if (x.getClassification().equals("dance")) {
                    matrix[2][2] = ++matrix[2][2];
                    auxRightness++;
                } else if (x.getClassification().equals("hip-hop/rap")) {
                    matrix[0][2] =  ++matrix[0][2];

                } else {
                    matrix[1][2] = ++matrix[1][2];

                }
            }
            aux.clear();

        }


        //vp -> verdadeiro positivo
        //fp -> falso positivo
        //fn -> falso negativo
        //vn -> verdadeiro negativo

        vpA = matrix[0][0];
        vpB = matrix[1][1];
        vpC = matrix[2][2];

        fpA = matrix[0][1] + matrix[0][2];
        fpB = matrix[1][0] + matrix[1][2];
        fpC = matrix[2][0] + matrix[2][1];


        fnA = matrix[1][0] + matrix[2][0];
        fnB = matrix[0][1] + matrix[2][1];
        fnC = matrix[1][2] + matrix[0][2];


        vnA = matrix[1][1] + matrix[2][1] + matrix[1][2] + matrix[2][2];
        vnB = matrix[0][0] + matrix[2][0] + matrix[0][2] + matrix[2][2];
        vnC = matrix[0][0] + matrix[1][0] + matrix[1][1] + matrix[0][1];



        double ac = (vpA + vpB + vpC + vnA + vnB + vnC)/instance.length;
        double err = (fpA + fpB + fpC + fnA + fnB + fnC)/instance.length;

        double precA = vpA / (vpA + fpA);
        double precB = vpB / (vpB + fpB);
        double precC = vpC / (vpC + fpC);

        double recallA = vpA / (vpA + fnA);
        double recallB = vpB / (vpB + fnB);
        double recallC = vpC / (vpC + fnC);

        double measureA = 2*((precA * recallA) / (precA + recallA));
        double measureB = 2*((precB * recallB) / (precB + recallB));
        double measureC = 2*((precC * recallC) / (precC + recallC));
        double measureTotal = (measureA + measureB + measureC)/2;




        System.out.printf("\n\nMatriz de confusão:\n\na\tb\tc\t<----Classificado como");
        for(int i = 0; i < matrix.length; i++) {
            System.out.println();
            for(int j = 0; j< matrix.length; j++) {
                System.out.print(matrix[i][j]+"\t");
            }
            if(i == 0) {
                System.out.printf("|\ta = hip-hop/rap");
            }else if(i == 1) {
                System.out.printf("|\tb = pop");
            }else {
                System.out.printf("|\tc = dance");
            }
        }



        System.out.printf("\n\nAcurácia: %.3f\nErro: %.3f\nPrecisão A: %.3f\nPrecisão B: %.3f\nPrecisao C: %.3f \nRecall A: %.3f\nRecall B: %.3f\nRecall C: %.3f\nMeasure A: %.3f\nMeasure B: %.3f\nMeasure C: %.3f\nMeasure Total: %.3f\n\n"
        ,ac, err, precA, precB, precC, recallA, recallB, recallC, measureA, measureB, measureC, measureTotal);
        System.out.println("Para K = "+k+"\nQuantidade de acerto: " +Double.valueOf(auxRightness).intValue() + "\nTamanho da base de testes: " + instance.length + "\nTamanho da base do knn: " + baseInstances.size());
        return ((auxRightness / instance.length) * 100); // this variable is responsible for showing this algorithm's rightness rate;

    }
}
