package net.digitallogic.ProjectManager.services;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

	private static final Pattern splitOnUnderscore = Pattern.compile("\\s*_\\s*");
	private static final Pattern splitOnComma = Pattern.compile("\\s*,\\s*");

	public static List<Order> processSortBy(String str) {
		return Arrays.stream(splitOnComma.split(str.trim()))
				// transform property to cameCase if it is in snake_case.
				.map(Utils::toCamel)
				.map(property -> {
					if (property.charAt(0) == '-')
						return new Order(Sort.Direction.DESC, property.substring(1));
					else
						return new Order(Sort.Direction.ASC, property);
				})
				.collect(Collectors.toList());
	}

	public static List<String> splitOnComma(String str) {
		return List.of(splitOnComma.split(str));
	}

	public static List<String> toCamel(String[] strs) {
		return toCamel(List.of(strs));
	}
	public static List<String> toCamel(List<String> strs) {
		return strs.stream()
				.map(Utils::toCamel)
				.collect(Collectors.toList());
	}

	public static String toCamel(String str) {
		String[] split = splitOnUnderscore.split(str.trim());
		StringBuilder sb = new StringBuilder(split[0]);

		for (int i = 1; i < split.length; ++i) {
			sb.append(split[i].substring(0, 1).toUpperCase())
				.append(split[i].substring(1).toLowerCase());
		}
		return sb.toString();
	}


	public static String toString(String[] arr) {
		StringBuilder sb = new StringBuilder();
		for (String str : arr) {
			sb.append(str)
					.append(" ");
		}
		return sb.substring(0, sb.length() - 1);
	}
}
