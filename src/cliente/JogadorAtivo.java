package cliente;

import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JogadorAtivo implements Runnable {
    private Socket servidorSala;
    private JanelaJogo janela;
    private InputStream salaIn;
    private PrintStream salaOut;
    private JSONObject requisicao;
    private JSONObject resposta;
    private Jogador jogador;

    public JogadorAtivo(Socket servidorSala, Jogador jogador) throws IOException {
        janela = new JanelaJogo(this);
        janela.setTitle("Jogo da Velha");
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);

        this.jogador = jogador;
        this.servidorSala = servidorSala;
        salaIn = servidorSala.getInputStream();
        salaOut = new PrintStream(servidorSala.getOutputStream());
    }


    public void novoJogo() {
        janela.dispatchEvent(new WindowEvent(janela, WindowEvent.WINDOW_CLOSING));
        janela = new JanelaJogo(this);
        janela.setTitle("jogo da velha");
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }

    public void sairJogo() throws IOException, Throwable {
        JSONObject resposta = new JSONObject();
        resposta.put("tipo", "desconectar");
        salaOut.println(resposta);
        servidorSala.close();
        jogador.sairSala();
        janela.setVisible(false);
        janela.dispose();//destrou o JFrame object
    }

    public void enviarJogada(int x, int y) throws JSONException { //jogada do jogador
        JSONObject jogada = new JSONObject();
        jogada.put("tipo", "jogada");
        jogada.put("x", x); // linha do tabuleiro
        jogada.put("y", y); // coluna do tabuleiro
        salaOut.println(jogada); // imprimi jogada
    }

    public void enviarMensagem(String mensagem) throws Throwable {
        requisicao = new JSONObject();
        requisicao.put("tipo", "mensagem");
        requisicao.put("mensagem", mensagem);
        salaOut.println(requisicao);

    }

    public void run() {
        Scanner scan = new Scanner(this.salaIn);
        String tipoSolicitacao = "";
        JSONObject resposta;
        while (scan.hasNextLine()) {//trata requisições de saida
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
            switch (tipoSolicitacao) {
                case "mensagem"://no caso de receber mensagem printa na tela
                    try {
                        janela.imprimirMensagem(requisicao.getString("mensagem"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "sairSala":
                    try {
                        //jogador expulso da sala
                        jogador.desconectar();
                        janela.setVisible(false);
                        janela.dispose();
                        this.finalize();
                    } catch (Throwable ex) {
                        Logger.getLogger(JogadorAtivo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "fimJogo":
                    Object[] options = {"sim", "não"};
                    //perguntar se o jogador quer jogar novamente
                    int outra = 0;
                    try {
                        outra = JOptionPane.showOptionDialog(new JFrame(),
                                requisicao.getString("mensagem")
                                        + "deseja jogar outra partida",
                                requisicao.getString("mensagem"),
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (outra == 0) { //jogar outra
                        novoJogo();
                    } else {
                        try {
                            sairJogo();
                        } catch (Throwable ex) {
                            Logger.getLogger(JogadorAtivo.class.getName()).log(Level.SEVERE, null, ex);

                        }
                    }
                    break;
                default:
                    break;
            }

        }
    }
}
