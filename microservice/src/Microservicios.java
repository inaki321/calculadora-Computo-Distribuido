public class Microservicios {
    public float sumaGet(float n1, float n2) {
        System.out.println("Numero 1 recibido "+n1);
        System.out.println("Numero 2 recibido "+n2);
        return n1 + n2;
    }

    public float restaGet(float n1, float n2) {
        System.out.println("Numero 1 recibido "+n1);
        System.out.println("Numero 2 recibido "+n2);
        return n1 - n2;
    }

    public float  multiGet(float n1, float n2) {
        System.out.println("Numero 1 recibido "+n1);
        System.out.println("Numero 2 recibido "+n2);
        return n1 * n2;
    }

    public float divGet(float n1, float n2) {
        System.out.println("Numero 1 recibido "+n1);
        System.out.println("Numero 2 recibido "+n2);
        return n1 / n2;
    }
}
