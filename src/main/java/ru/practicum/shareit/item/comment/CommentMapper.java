package ru.practicum.shareit.item.comment;

public class CommentMapper {
    public static Comment dtoToComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public static CommentDto commentToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());

        return commentDto;
    }
}
