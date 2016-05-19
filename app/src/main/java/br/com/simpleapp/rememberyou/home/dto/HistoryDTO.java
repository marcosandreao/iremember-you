package br.com.simpleapp.rememberyou.home.dto;

import java.util.HashMap;
import java.util.Map;

import br.com.simpleapp.rememberyou.entity.History;

/**
 * Created by socram on 30/04/16.
 */
public class HistoryDTO extends History{
    public Map<String, Integer> countEmotions = new HashMap<>();
}
