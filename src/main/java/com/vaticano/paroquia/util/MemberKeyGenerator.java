package com.vaticano.paroquia.util;

import com.vaticano.paroquia.domain.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberKeyGenerator {

    /**
     * Gera chave única do membro baseada em:
     * nomeCompleto + dataNascimento + nomePai + nomeMae
     *
     * Esta chave é usada para detectar duplicados na importação CSV.
     */
    public String generateMemberKey(String nomeCompleto, String dataNascimento,
                                    String nomePai, String nomeMae) {
        String normalized =
            NormalizeUtil.normalizeForKey(NormalizeUtil.normalizeValue(nomeCompleto)) +
            NormalizeUtil.normalizeForKey(NormalizeUtil.normalizeValue(dataNascimento)) +
            NormalizeUtil.normalizeForKey(NormalizeUtil.normalizeValue(nomePai)) +
            NormalizeUtil.normalizeForKey(NormalizeUtil.normalizeValue(nomeMae));

        // Se a chave estiver vazia (todos os campos vazios), gera chave temporária
        if (normalized.isBlank()) {
            return "temp_" + System.currentTimeMillis();
        }

        return normalized;
    }

    /**
     * Gera chave única do membro a partir de uma entidade Member.
     */
    public String generateMemberKey(Member member) {
        return generateMemberKey(
            member.getNomeCompleto(),
            member.getDataNascimento(),
            member.getNomePai(),
            member.getNomeMae()
        );
    }

    /**
     * Deriva se o membro está batizado baseado na data de batismo.
     */
    public boolean deriveBatizado(String dataBaptismo) {
        String normalized = NormalizeUtil.normalizeValue(dataBaptismo);
        return !normalized.isEmpty();
    }

    /**
     * Deriva se o membro está crismado baseado na data de crisma.
     */
    public boolean deriveCrismado(String dataCrisma) {
        String normalized = NormalizeUtil.normalizeValue(dataCrisma);
        return !normalized.isEmpty();
    }

    /**
     * Deriva se o membro está casado baseado na data de casamento.
     */
    public boolean deriveCasado(String dataCasamento) {
        String normalized = NormalizeUtil.normalizeValue(dataCasamento);
        return !normalized.isEmpty();
    }
}
