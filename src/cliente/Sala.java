package cliente;
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
import java.util.logging.Level;
import java.util.logging.Logger;



public class Sala implements Runnable{
    private ServerSocket servidor;
    private String ipHost;
    private int maxJogadores;
    private String nomeSala;
    private List<TrataJogador> jogadores;
    private int[][] tabuleiro = new int[3][3];
    int lastID;
    int vezJogador;

    public Sala(String nomeSala) throws IOException{
        jogadores = new ArrayList<>();
        servidor = new ServerSocket(12345);
        servidor.setReuseAddress(true);
        ipHost = servidor.getInetAddress().getHostAddress();
        vezJogador = 1;
        lastID = 1;
    }
    public void resetarTabuleiro(){
        tabuleiro = new int[3][3];
    }
    public void Jogada(int idJogador,int x, int y){ //marca a jogada no tabuleiro idJogador = é o jogador que fez a jogada
        int vitoria;
        if(idJogador == 1){
            tabuleiro[x][y]=1;
        }else if(idJogador == 1){
            tabuleiro[x][y] = -1;
        }
        vezJogador = (vezJogador == 1) ? 2:1; //alterna a vez
    }
    public boolean fimJogo(){
        int vitoria = vitoria();

        if(vitoria != 0){
            return true;
        }else{
            return false;
        }

    }
    public int vitoria(){
        int retorno = 0;
        if(checaLinhas()!=0){
            retorno = checaLinhas();
        } else if (checaColunas()!=0) {
            retorno = checaColunas();

        }else if(checaDiagonais()!=0){
            retorno = checaDiagonais();
        }else if(tabuleiroPreenchido()){ //empate
            retorno = -1;
        }
        return retorno;
    }
    public boolean tabuleiroPreenchido(){
        int soma = 0;

        for(int x = 0; x<3; x++){
            for(int y=0;y<3;y++){
                if(tabuleiro[x][y]!=0){
                    soma++;
                }
            }
        }
        return (soma == 9) ? true : false;
    }
    public int checaLinhas(){
        for(int linha = 0; linha < 3; linha ++){
            if((tabuleiro[linha][0]+tabuleiro[linha][1]+tabuleiro[linha][2]==-3)){
                return 2;
            }
            if((tabuleiro[linha][0]+tabuleiro[linha][1]+tabuleiro[linha][2]==3)){
                return 1;
            }
        }
        return 0;
    }
    public int checaColunas(){
        for(int coluna = 0; coluna < 3; coluna ++){
            if((tabuleiro[0][coluna]+tabuleiro[1][coluna]+tabuleiro[2][coluna]==-3)){
                return 2;
            }
            if((tabuleiro[0][coluna]+tabuleiro[1][coluna]+tabuleiro[2][coluna]==3)){
                return 1;
            }
        }
        return 0;
    }
    public int checaDiagonais(){
        if((tabuleiro[0][0]+tabuleiro[1][1]+tabuleiro[2][2]==-3)){
            return 2;
        }
        if((tabuleiro[0][0]+tabuleiro[1][1]+tabuleiro[2][2]==3)){
            return 1;
        }
        if((tabuleiro[0][2]+tabuleiro[1][1]+tabuleiro[2][0]==-3)){
            return 2;
        }
        if((tabuleiro[0][2]+tabuleiro[1][1]+tabuleiro[2][0]==3)){
            return 1;
        }
        return 0;
    }
    public void fecharSala()throws IOException,Throwable{
        System.out.println("fechar");
        this.servidor.close();
        this.finalize();
    }
    public void removerJogadores(int idJogador){
        for(int i=0;i<jogadores.size();i++){
            if(jogadores.get(i).idJogador == idJogador){
                jogadores.remove(i);
                lastID = idJogador;
            }
        }

    }
    public List<TrataJogador> getListaJogadores(){
        return jogadores;
    }
    public String getIpHost(){

        return this.ipHost;
    }
    public void setIpHost(String novoIp){

        this.ipHost = novoIp;
    }
    public void run(){
        try{
            while (true){
                Socket cliente = servidor.accept();
                boolean isHost = (this.ipHost.equals(cliente.getInetAddress().getHostAddress()));
                TrataJogador tc = new TrataJogador(cliente,this,"jogador"+(lastID),lastID,isHost);
                lastID = (lastID ==1) ? 2:1;
                jogadores.add(tc);
                new Thread(tc).start();
            }
        }catch (IOException ex){
            Logger.getLogger(Sala.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
}
class TrataJogador implements Runnable {
    private InputStream clienteIn;
    private PrintStream clienteOut;
    private JSONObject requisicao;
    private JSONObject resposta;
    String nomeJogador;
    int idJogador;
    private Sala sala;
    private List<TrataJogador> jogadores;
    public boolean isHostSala;
    private Socket cliente;

    public TrataJogador(Socket cliente, Sala sala, String nomeJogador, int id, boolean host) throws IOException {
        System.out.println("novo jogador");

        this.isHostSala = host;
        this.idJogador = id;
        this.nomeJogador = nomeJogador;
        this.clienteIn = cliente.getInputStream();
        this.clienteOut = new PrintStream(cliente.getOutputStream());
        this.sala = sala;

    }

    public void enviarMensagem(String mensagem) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("tipo", "mensagem");
        obj.put("mensagem", mensagem);
        clienteOut.println(obj);
    }

    public void enviarJogada(int x, int y, int idJogador) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("x", x);
        obj.put("y", y);
        obj.put("idJogador", idJogador);
        clienteOut.println(obj);
    }

    public void fimJogo() throws JSONException {
        int vitorioso = sala.vitoria();
        JSONObject obj = new JSONObject();
        obj.put("tipo", "fimJogo");
        String mensagem = "";

        if (vitorioso == -1) {
            mensagem = "empate";
        } else if (vitorioso == idJogador) {
            mensagem = "voce Venceu";
        } else {
            mensagem = "voce perdeu";
        }
        obj.put("mensagem", mensagem);
        clienteOut.println(obj);

    }

    public void desconectar() throws Throwable {
        cliente.close();
        this.finalize();
    }

    public void run() {
        Scanner scan = new Scanner(this.clienteIn);
        String tipoSolicitacao = "";
        JSONObject resposta;

        while (scan.hasNextLine()) {
            requisicao = new JSONObject(scan.hasNextLine());

            try {
                tipoSolicitacao = requisicao.getString("tipo");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            switch (tipoSolicitacao) {
                case "mensagem": // envia mensagem para todos os jogadores
                    jogadores = sala.getListaJogadores();
                    for (int i = 0; i < jogadores.size(); i++) {
                        try {
                            jogadores.get(i).enviarMensagem(nomeJogador + ": " + requisicao.getString("mensagem"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case "desconectar": //desconectar o jogador
                    try {
                        if (!isHostSala) {
                            desconectar();
                        } else {
                            sala.fecharSala();
                        }
                    } catch (Throwable ex) {
                        Logger.getLogger(TrataJogador.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        }

    }
}