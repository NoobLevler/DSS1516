package AAE;

import DAOs.EleicaoDAO;
import DAOs.MapaEleitoralDAO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by drcon on 15/12/2015.
 */
public class AAE {

    private EleicaoDAO eleicaoDAO;
    private CandidaturaDAO candidaturaDAO;
    private MapaEleitoralDAO mapaEleitoralDAO;
    private ListaDAO listaDAO;
    private EleitorDAO eleitorDAO;

    public void adicionarCandidato(Eleicao eleicao, GregorianCalendar data_bi, int bi, String arquivo, String filicao, String nome, String profissao, int idade, String morada, String nacionalidade, GregorianCalendar data)
    {
        Candidato c = new Candidato(data_bi, bi, filicao, arquivo, nome, profissao, idade, morada, nacionalidade);
        Candidatura can = new Candidatura(candidaturaDAO.getAvailableId(), data, c, eleicao);
        boolean valid = can.validarCandidatura();
        candidaturaDAO.addCandidatura(can);

    }

    public void adicionarCirclo(String distrito, int eleitores, int deputados) throws DistritoInvalidoException {
        if(!Distritios.distiritos.contains(distrito))
            throw new DistritoInvalidoException();
        MapaEleitoral c = new MapaEleitoral(distrito, eleitores,deputados);
        mapaEleitoralDAO.addMapa(c);
    }

    public void adicionarLista(String eleicao, String nome, String circulo, ArrayList<Integer> deputados, ArrayList<Integer> delegados)
    {


        ArrayList<Eleitor> deps = new ArrayList<>();

        for(Integer i : deputados)
        {
            Eleitor eleitor = EleitorDAO.getEleitor(i);
            deps.add(eleitor);
        }

        ArrayList<Eleitor> dels = new ArrayList<>();

        for(Integer i : delegados)
        {
            Eleitor eleitor = EleitorDAO.getEleitor(i);
            dels.add(eleitor);
        }

        Lista l = new Lista(nome, circulo, deps,dels);

        try {
            validarLista(l);
        } catch (DeputadoJaPertenceAListaException e) {
            e.printStackTrace();
        } catch (DelegadoJaPertenceAListaException e) {
            e.printStackTrace();
        } catch (NomeDeListaRepetidoException e) {
            e.printStackTrace();
        }

        Eleicao e = eleicaoDAO.getEleicao(eleicao);

        e.registarLista(l);
    }

    public void validarLista(Lista l) throws DeputadoJaPertenceAListaException, DelegadoJaPertenceAListaException, NomeDeListaRepetidoException {
        ArrayList<Lista> listas = listaDAO.getListas();

        for(Lista lista : listas)
        {
            ArrayList<Eleitor> deputados = l.getDeputados();
            for(Eleitor e : deputados)
            {
                if(lista.temDeputado(e))
                {
                    throw new DeputadoJaPertenceAListaException();
                }
            }

            for(Eleitor e : l.getDelegados())
            {
                if(lista.temDelegado(e))
                {
                    throw new DelegadoJaPertenceAListaException();
                }
            }

            if(lista.getNome().equals(l.getNome()))
            {
                throw new NomeDeListaRepetidoException();
            }
        }
    }

    public void adicionarVotantes(String path) throws FileNotFoundException, IOException {

        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String line;
        while(( line = reader.readLine()) != null)
        {
            sb.append(line);
        }
        reader.close();
        eleitorDAO = new EleitorDAO(sb.toString()); //SQL to create the table
        
    }


    public void adicionarAssembleia(String e, String freg, String presNome, int presID,String vPresNome, int vPresID, String secNome, int secID, ArrayList<String> escNomes, ArrayList<Integer> escIDs  )
    {
        Eleicao el = eleicaoDAO.getEleicao(e);

        Eleitor pres = EleitorDAO.getEleitor(presID);

        Eleitor vPres = EleitorDAO.getEleitor(vPresID);

        Eleitor sec = EleitorDAO.getEleitor(secID);

        ArrayList<Eleitor> esc = new ArrayList<>();

        for(Integer i : escIDs)
        {
            Eleitor eleitor = EleitorDAO.getEleitor(i);
            esc.add(eleitor);
        }

        AssembleiaDeVoto assembleiaDeVoto = new AssembleiaDeVoto(freg, pres, vPres, sec, esc);

        el.registarAssembleia(assembleiaDeVoto);


    }


    public void atribuirMandatos(String eleicao, String list, int deps)
    {
        Eleicao e = eleicaoDAO.getEleicao(eleicao);

        e.atribuirMandatos(list, deps);
    }


    public void gerarEstatisticas(Eleicao e)
    {

        e.gerarEstatisticas();

    }
    public static void main(String[] args) {

    }
}