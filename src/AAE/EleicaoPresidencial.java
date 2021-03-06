package AAE;

import DAOs.CandidatoDAO;
import DAOs.CandidaturaDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by drcon on 28/12/2015.
 */
public class EleicaoPresidencial extends Eleicao{
    private CandidatoDAO candidatos;
    private CandidaturaDAO candidaturas;

    public EleicaoPresidencial(String nome, java.sql.Date data) {
        super(nome,data, "presidenciais");
    }
    public HashMap<Candidato, Integer> gerarEstatisticas()
    {
        HashMap<Candidato, Integer> toRet = new HashMap<>();
        Collection<Candidato> ca = candidatos.values();

        for(Candidato candidato : ca)
        {

            toRet.put(candidato, candidatos.getVotos(candidato.getBi(),this.Nome));
        }
        return toRet;
    }
}
