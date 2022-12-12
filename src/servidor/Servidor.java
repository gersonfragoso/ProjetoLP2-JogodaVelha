package servidor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class Jogador {
    public Socket cliente;
    public String ip;
    public String apelido;
    public boolean isHostSala;

    public Jogador(Socket cliente) {
        this.cliente =cliente;
        isHostSala =false;
        int aleatorio = (int) (Math.random()*1000);
        this.apelido ="jogador"+aleatorio;
        this.ip =cliente.getInetAddress().getHostAddress();
    }
}
class Sala{
    Sala(int idSala,String nomeSala, int maxJogadores, String ipHost, int numJogadores) {
        this.idSala = idSala;
        this.ipHost = ipHost;
        this.nomeSala = nomeSala;
        this.maxJogadores = maxJogadores;
        this.numJogadores = numJogadores;
    }
    public JSONObject toJSON() throws Throwable,JSONException{
        JSONObject obj = new JSONObject();
        obj.put("idSala", this.idSala);
        obj.put("maxJogadores",this.maxJogadores);
        obj.put("ipHost",this.ipHost);
        obj.put("numJogadores",this.numJogadores);
        obj.put("nomeSala",this.nomeSala);
        return obj;
    }
    public String ipHost;
    public String nomeSala;
    public int maxJogadores;
    public int numJogadores;
    public int idSala;
}
public class Servidor {
    private int porta = 12345;
    private List<Jogador> jogadores;
    private List<JSONObject> salas;
    private int lastIdSala;
    public Servidor(int porta){
        this.lastIdSala = -1;
        this.porta = porta;
        jogadores = new ArrayList<>();
        salas = new ArrayList<>();
    }
    public int getidSala(){
        lastIdSala +=1;
        return lastIdSala;
    }
    public JSONObject listarSalas(){
        return new JSONObject();
    }
    public int criarSala(String nomeSala, int maxJogadores, String IpHost) throws Throwable {
        int id = getidSala();
        Sala sala = new Sala(id,nomeSala,maxJogadores,IpHost,1);
        salas.add(sala.toJSON());
        System.out.println("Sala criada"+nomeSala);
        return id;
    }
    public void excluirSala(int idSala) throws JSONException {
        for(int i = 0; i < salas.size(); i++){
            if (salas.get(i).getInt("idSala")==idSala){
                salas.remove(i);
            }
        }
    }
    public void removerJogadorSala(int idSala) throws JSONException {
        for(int i = 0;i< salas.size();i++){
            if(salas.get(i).getInt("idSala")==idSala);
            int n = salas.get(i).getInt("numJogadores");
            salas.get(i).remove("numJogadores");
            salas.get(i).put("numJogadores", n-1);
            break;
        }
    }
    public void broadcast(String mensagem){

    }
    public void executa() throws IOException{
        ServerSocket servidor = new ServerSocket(this.porta);
        System.out.println("porta 12345 aberta");
        while(true){
            //aceita o cliente
            Socket cliente = servidor.accept();
            System.out.println("Nova Conexão com o Cliente" + cliente.getInetAddress().getHostAddress());
            Jogador jogador= new Jogador(cliente);
            this.jogadores.add(jogador);
            //cria um cliente em uma thread nova
            TrataCliente tc = new TrataCliente(jogador,this);
            new Thread(tc).start();
        }
    }
    class TrataCliente implements Runnable{
        private InputStream clienteIn;
        private PrintStream clienteOut;
        private Servidor servidor;
        private Jogador jogador;
        JSONObject requisicao;
        public int idSala;
        public boolean isHostSala;
        public TrataCliente(Jogador jogador, Servidor servidor) throws IOException{
            this.clienteIn=jogador.cliente.getInputStream();
            this.clienteOut=new PrintStream(jogador.cliente.getOutputStream());
            this.servidor=servidor;
            this.jogador=jogador;
            this.idSala= -1;
            this.isHostSala=false ;
        }
        public void run(){
            Scanner scan = new Scanner(this.clienteIn);
            String tipoSolicitacao = "";
            JSONObject resposta;
            while (scan.hasNextLine()){
                try {
                    requisicao = new JSONObject(scan.nextLine());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    tipoSolicitacao = requisicao.getString("tipo");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                switch (tipoSolicitacao){
                    case "setApelido":
                        try {
                            String apelido = requisicao.getString("apelido");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    case "entrarSala":
                        for(int i = 0; i < salas.size();i++){
                            try {
                                if(salas.get(i).getString("ipHost").equals(requisicao.getString("ipHost"))){
                                    JSONObject obj = salas.get(i);
                                    //checar se a sala tem espaço para jogadores
                                    if(obj.getInt("numJogador") < obj.getInt("maxJogadores")){ // caso sim
                                        int n  = salas.get(i).getInt("numJogadores");
                                        salas.get(i).remove("numJogadores");
                                        salas.get(i).put("numJogadores",n+1);
                                        resposta = new JSONObject();
                                        resposta.put("tipo","respostaEntrarSala");
                                        resposta.put("aceita", true);
                                        clienteOut.println(resposta);
                                    }else{//caso nao
                                        resposta = new JSONObject();
                                        resposta.put("tipo","respostaEntrarSala");
                                        resposta.put("aceita", false);
                                        resposta.put("mensagemErro","cheia");
                                        clienteOut.println(resposta);

                                    }
                                    break;
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                    case "criarSala": //solicitação cria nova sala
                        String nomeSala = null;
                        try {
                            nomeSala = requisicao.getString("nomeSala");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        int maxJog = 0;
                        try {
                            maxJog = requisicao.getInt("maxJogadores");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        String ipHost = jogador.ip;
                        try {
                            this.idSala = servidor.criarSala(nomeSala,maxJog,ipHost);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                        this.isHostSala = true;
                        break;
                    case "sairSala":
                        if(!this.isHostSala){
                            try {
                                servidor.removerJogadorSala(this.idSala);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            try {
                                servidor.excluirSala(this.idSala);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        isHostSala = false;
                        idSala = -1;
                        break;
                    case "destruirSala":
                        if(this.isHostSala){
                            try {
                                servidor.excluirSala(this.idSala);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                        break;
                    case "getListaSalas": //solicitação para obter a lista de salas
                        resposta = new JSONObject();
                        try {
                            resposta.put("listaSalas",salas);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            JSONArray respostaJSONArray = resposta.getJSONArray("listaSalas");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                        clienteOut.println(resposta);
                        break;
                    default:
                }

            }
            scan.close();

        }
    }

    public static void main(String[] args) throws IOException {
        new Servidor(12345).executa();

    }
}