/*
 * Copyright [2011-2016] "Neo Technology"
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 */
package org.neo4j.cineasts.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.neo4j.cineasts.movieimport.MovieDbImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author mh
 * @since 04.03.11
 */
@Controller
public class ImportController {

    private static final Logger log = LoggerFactory.getLogger(ImportController.class);
    private MovieDbImportService importService;

    @Autowired
    public ImportController(MovieDbImportService importService) {
        this.importService = importService;
    }

    @RequestMapping(value = "/import/movie/{ids}", method = RequestMethod.GET)
    public String importMovie(@PathVariable String ids, Model model) {
        long start = System.currentTimeMillis();
        final Map<Integer, String> movies = importService.importMovies(extractRanges(ids));
        long duration = (System.currentTimeMillis() - start) / 1000;
        model.addAttribute("duration", duration);
        model.addAttribute("ids", ids);
        model.addAttribute("movies", movies.entrySet());
        return "import/result";
    }

    @RequestMapping(value = "/import/tv/{ids}", method = RequestMethod.GET)
    public String importTV(@PathVariable String ids, Model model) {
        long start = System.currentTimeMillis();
        final Map<Integer, String> tvs = importService.importTVs(extractRanges(ids));
        long duration = (System.currentTimeMillis() - start) / 1000;
        model.addAttribute("duration", duration);
        model.addAttribute("ids", ids);
        model.addAttribute("movies", tvs.entrySet());
        return "import/result";
    }

    private Map<Integer, Integer> extractRanges(String ids) {
        Map<Integer, Integer> ranges = new LinkedHashMap<Integer, Integer>();
        StringBuilder errors = new StringBuilder();
        for (String token : ids.split(",")) {
            try {
                if (token.contains("-")) {
                    String[] range = token.split("-");
                    ranges.put(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
                } else {
                    int id = Integer.parseInt(token);
                    ranges.put(id, id);
                }
            } catch (Exception e) {
                errors.append(token).append(": ").append(e.getMessage()).append("\n");
            }
        }
        if (errors.length() > 0) {
            throw new RuntimeException("Error parsing ids\n" + errors);
        }
        return ranges;
    }
}
