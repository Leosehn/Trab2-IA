// Classe do Nodo separada para melhor organização.
// [mvfm]

// Criado : 10/11/2025  ||  Última vez alterado : 10/11/2025

import java.util.Objects;

class Nodo{
    // Seu "valor", caracter que ele guarda.
    String valor;

    // Sua "posição."
    int x, y;

    int g,      // Caminho percorrido.
        h,      // Quanto falta.
        f;      // Custo total, (g + h).

    // Nodo anterior do caminho.
    Nodo parente;

    Nodo(int x, int y, String valor){
        this.x = x;
        this.y = y;
        this.valor = valor;
    }

    // Override do equals && hash para comparar nodos.
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Nodo nodo = (Nodo) obj;
        return x == nodo.x && y == nodo.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }

}