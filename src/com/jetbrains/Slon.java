import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Slon {

    public static void main(String[] args) {
        final BigInteger[] bi = new BigInteger[1];
        bi[0] = BigInteger.valueOf(0);
        boolean haveAllTheSameMas = true;

        Scanner scanner = new Scanner(System.in);
        int liczbaSloni = scanner.nextInt();

        Elephant[] elephants = new Elephant[liczbaSloni];

        for (int indexForInput = 0; indexForInput < liczbaSloni; indexForInput++) {
            elephants[indexForInput] = new Elephant(scanner.nextInt());
            elephants[indexForInput].name = indexForInput + 1;

            if (haveAllTheSameMas && indexForInput > 0) {
                if (elephants[indexForInput].masa != elephants[indexForInput - 1].masa)
                    haveAllTheSameMas = false;
            }
        }

        for (int indexForInput = 0; indexForInput < liczbaSloni; indexForInput++) {
            elephants[scanner.nextInt() - 1].index = indexForInput + 1;
        }

        for (int indexForInput = 0; indexForInput < liczbaSloni; indexForInput++) {
            elephants[scanner.nextInt() - 1].wantedIndex = indexForInput + 1;
        }

        List<List<Elephant>> rotations = new LinkedList<>();
        List<Elephant> sortedElephants =
                Arrays.stream(elephants).sorted(Comparator.comparingInt(el -> el.index)).collect(Collectors.toList());

        for (int i = 0; i < liczbaSloni; i++) {
            if (elephants[i].isInRotation) {
                continue;
            }
            if (elephants[i].index == elephants[i].wantedIndex) {
                elephants[i].isInRotation = true;
                continue;
            }

            List<Elephant> curRotation = new LinkedList<>();
            Elephant firstElephant = elephants[i];
            Elephant tmp = firstElephant;

            do {
                curRotation.add(tmp);
                tmp.isInRotation = true;
                tmp = sortedElephants.get(tmp.wantedIndex - 1);
            } while (tmp != firstElephant && curRotation.size() != elephants.length);

            rotations.add(curRotation);

            if (rotations.get(0).size() == liczbaSloni)
                break;
        }
        bi[0] = bi[0].add(rotateWith2(rotations));

        rotations = rotations.stream()
                .filter(rot -> rot.size() != 2)
                .collect(Collectors.toList());

        bi[0] = bi[0].add(rotateWith3(rotations));

        rotations = rotations.stream()
                .filter(rot -> rot.size() != 3)
                .collect(Collectors.toList());

        if (haveAllTheSameMas) {
            rotations.forEach(rotation -> {
                bi[0] = bi[0].add(evaluateForMethod1(rotation,
                        new Elephant[]{rotation.get(rotation.size() - 1)}, rotation.size() - 1));
            });
            System.out.println(bi[0]);
            return;
        }

        final Elephant[] min = new Elephant[1];
        rotations.forEach(list -> {
            if (min[0] == null)
                min[0] = list.get(0);
            list.forEach(el -> {
                if (el.masa < min[0].masa)
                    min[0] = el;
            });
        });

        rotations.forEach(rotation -> {
            final Elephant[] localMin = new Elephant[1];
            int indexOfCurMinInLocalC = 0;

            List<Elephant> sortedRotation = rotation.stream()
                    .sorted(Comparator.comparingInt(el -> el.masa))
                    .collect(Collectors.toList());

            localMin[0] = sortedRotation.get(0);
            indexOfCurMinInLocalC = rotation.indexOf(localMin[0]);

            BigInteger method1 = evaluateForMethod1(rotation, localMin, indexOfCurMinInLocalC);
            BigInteger method2 = (min[0] != localMin[0])
                    ? evaluateForMethod2(rotation, localMin, indexOfCurMinInLocalC, min)
                    : evaluateForMethod1(rotation, min, indexOfCurMinInLocalC);

            bi[0] = bi[0].add(
                    (method1.compareTo(method2) < 0)
                            ? method1
                            : method2);
        });

        System.out.println(bi[0]);
    }

    private static BigInteger evaluateForMethod2(List<Elephant> list, final Elephant[] localMin, int indexOfCurMinInLocalC, final Elephant[] min) {
        final BigInteger[] bi = new BigInteger[1];
        bi[0] = BigInteger.valueOf(0);

        Elephant[] arr = new Elephant[list.size()];
        AtomicInteger ind = new AtomicInteger(0);
        list.forEach(elephant -> {
            arr[ind.get()] = elephant;
            ind.addAndGet(1);
        });
        bi[0] = bi[0].add(swap2(localMin[0], min[0]));
        indexOfCurMinInLocalC--;
        if (indexOfCurMinInLocalC < 0) {
            indexOfCurMinInLocalC = list.size() - 1;
        }

        for (int i = 0; i < list.size() - 1; i++) {
            bi[0] = bi[0].add(swap2(min[0], arr[indexOfCurMinInLocalC]));

            indexOfCurMinInLocalC--;
            if (indexOfCurMinInLocalC < 0) {
                indexOfCurMinInLocalC = list.size() - 1;
            }
        }

        bi[0] = bi[0].add(swap2(localMin[0], min[0]));
        return bi[0];
    }

    private static BigInteger evaluateForMethod1(List<Elephant> rotation, final Elephant[] localMin, int indexOfCurMinInLocalC) {
        final BigInteger[] bi = new BigInteger[1];
        bi[0] = BigInteger.valueOf(0);

        Elephant[] arr = new Elephant[rotation.size()];
        AtomicInteger ind = new AtomicInteger(0);
        rotation.forEach(elephant -> {
            arr[ind.get()] = elephant;
            ind.addAndGet(1);
        });
        indexOfCurMinInLocalC--;
        if (indexOfCurMinInLocalC < 0) {
            indexOfCurMinInLocalC = rotation.size() - 1;
        }
        for (int i = 0; i < rotation.size() - 1; i++) {
            bi[0] = bi[0].add(swap2(localMin[0], arr[indexOfCurMinInLocalC]));
            indexOfCurMinInLocalC--;
            if (indexOfCurMinInLocalC < 0) {
                indexOfCurMinInLocalC = rotation.size() - 1;
            }
        }
        return bi[0];
    }

    public static BigInteger swap2(Elephant el1, Elephant el2) {
        BigInteger bi = BigInteger.valueOf(0);
        el1.index = el1.wantedIndex;
        el2.index = el2.wantedIndex;
        bi = bi.add(BigInteger.valueOf(el1.masa));
        bi = bi.add(BigInteger.valueOf(el2.masa));
        return bi;
    }

    public static BigInteger rotateWith2(List<List<Elephant>> rotations) {
        final BigInteger[] bi = new BigInteger[1];
        bi[0] = BigInteger.valueOf(0);
        rotations.stream()
                .filter(rot -> rot.size() == 2)
                .map(listOfRot -> swap2(listOfRot.get(0), listOfRot.get(1)))
                .forEach(intt -> {
                    bi[0] = bi[0].add(intt);
                });
        return bi[0];
    }

    public static BigInteger rotateWith3(List<List<Elephant>> rotations) {
        final BigInteger[] bi = new BigInteger[1];
        bi[0] = BigInteger.valueOf(0);
        rotations.stream().filter(rot -> rot.size() == 3).forEach(listOfRot -> {
            Elephant minMas = listOfRot.stream().min(Comparator.comparingInt(o -> o.masa)).get();
            Elephant avgMas = listOfRot.stream().filter(el -> el != minMas).min(Comparator.comparingInt(o -> o.masa)).get();
            Elephant maxMas = listOfRot.stream().max(Comparator.comparingInt(o -> o.masa)).get();

            bi[0] = bi[0].add(swap2(minMas, avgMas));
            bi[0] = bi[0].add(swap2(minMas, maxMas));
        });
        return bi[0];
    }
}

class Elephant {
    public int name;
    public int masa;
    public int index;
    public int wantedIndex;
    public boolean isInRotation;

    public Elephant(int masa) {
        this.masa = masa;
        isInRotation = false;
    }
}
