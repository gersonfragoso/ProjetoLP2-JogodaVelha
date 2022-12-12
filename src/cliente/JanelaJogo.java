package cliente;
import org.json.JSONException;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JanelaJogo extends javax.swing.JFrame {
    //criar a janela do jogo
    public JanelaJogo(JogadorAtivo jogador){
        this.jogador= jogador;
        initComponents();



    }

    private void initComponents() {
    }

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) throws Throwable {
        jogador.enviarMensagem(jTextField1.getText());

    }
    private void pos00ActionPerformed(java.awt.event.ActionEvent evt) throws JSONException {
        jogador.enviarJogada(0,0);

    }
    private void pos10ActionPerformed(java.awt.event.ActionEvent evt) throws JSONException {
        jogador.enviarJogada(1,0);

    }
    private void pos20ActionPerformed(java.awt.event.ActionEvent evt) throws JSONException {
        jogador.enviarJogada(2,0);

    }
    private void pos01ActionPerformed(java.awt.event.ActionEvent evt) throws JSONException {
        jogador.enviarJogada(0,1);

    }
    private void pos11ActionPerformed(java.awt.event.ActionEvent evt)throws JSONException{
        jogador.enviarJogada(1,1);

    }
    private void pos21ActionPerformed(java.awt.event.ActionEvent evt)throws JSONException{
        jogador.enviarJogada(2,1);

    }
    private void pos12ActionPerformed(java.awt.event.ActionEvent evt)throws JSONException{
        jogador.enviarJogada(1,2);

    }
    private void pos22ActionPerformed(java.awt.event.ActionEvent evt)throws JSONException{
        jogador.enviarJogada(2,2);

    }
    private void formWindowClosed(java.awt.event.ActionEvent evt) throws Throwable {
        jogador.sairJogo();
        Logger.getLogger(JanelaJogo.class.getName()).log(Level.SEVERE, (String) null);
    }
    public void atribuirJogada(JButton botao, ImageIcon simbolo){
        botao.setIcon(simbolo);
        botao.setDisabledIcon(simbolo);
        botao.setEnabled(false);
    }
    public void imprimirMensagem(String mensagem){
        console.append(mensagem+"\n");
    }
    public static void main(String arg []){
    }
    private JogadorAtivo jogador;
    //Variables declararion - do not modify //GEN-BEGIN:variables
    private javax.swing.JTextArea console;
    private javax.swing.JButton jButton10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton pos00;
    private javax.swing.JButton pos01;
    private javax.swing.JButton pos02;
    private javax.swing.JButton pos10;
    private javax.swing.JButton pos11;
    private javax.swing.JButton pos12;
    private javax.swing.JButton pos20;
    private javax.swing.JButton pos21;
    private javax.swing.JButton pos22;

    //
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

}

